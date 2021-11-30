package net.ssehub.jacat.addon.exercise_submitter_server_datacollector;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;

import net.ssehub.jacat.addon.exercise_submitter_server_datacollector.config.ExerciseSubmitterServerConfig;
import net.ssehub.jacat.api.addon.data.AbstractDataCollector;
import net.ssehub.jacat.api.addon.data.DataProcessingRequest;
import net.ssehub.jacat.api.addon.data.Submission;
import net.ssehub.jacat.api.addon.data.SubmissionCollection;
import net.ssehub.studentmgmt.backend_api.api.AssignmentApi;
import net.ssehub.studentmgmt.backend_api.api.AssignmentRegistrationApi;
import net.ssehub.studentmgmt.backend_api.api.CourseApi;
import net.ssehub.studentmgmt.backend_api.api.CourseParticipantsApi;
import net.ssehub.studentmgmt.backend_api.model.AssignmentDto;
import net.ssehub.studentmgmt.backend_api.model.AssignmentDto.CollaborationEnum;
import net.ssehub.studentmgmt.backend_api.model.CourseDto;
import net.ssehub.studentmgmt.backend_api.model.GroupDto;
import net.ssehub.studentmgmt.backend_api.model.ParticipantDto;
import net.ssehub.studentmgmt.sparkyservice_api.api.AuthControllerApi;
import net.ssehub.studentmgmt.sparkyservice_api.model.AuthenticationInfoDto;
import net.ssehub.studentmgmt.sparkyservice_api.model.CredentialsDto;
import net.ssehub.studentmgmt.sparkyservice_api.model.TokenDto;
import net.ssehub.teaching.exercise_submitter.server.api.ApiClient;
import net.ssehub.teaching.exercise_submitter.server.api.ApiException;
import net.ssehub.teaching.exercise_submitter.server.api.api.SubmissionApi;
import net.ssehub.teaching.exercise_submitter.server.api.model.FileDto;

public class ExerciseSubmitterServerDataCollector extends AbstractDataCollector {

    private static final DateTimeFormatter TOKEN_EXPIRATION_PARSER = new DateTimeFormatterBuilder()
            .parseLenient()
            .parseCaseInsensitive()
            .appendValue(ChronoField.MONTH_OF_YEAR, 2)
            .appendLiteral('/')
            .appendValue(ChronoField.DAY_OF_MONTH, 2)
            .appendLiteral('/')
            .appendValue(ChronoField.YEAR, 2)
            .appendLiteral(' ')
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .toFormatter(Locale.ROOT);
    
    private Logger logger;
    
    private ExerciseSubmitterServerConfig configuration;
    
    private Path workdir;
    
    private ApiClient submissionServerClient;
    
    private net.ssehub.studentmgmt.backend_api.ApiClient stuMgmtClient;
    
    private AuthControllerApi authApi;

    private Instant currentTokenExpiration;
    
    public ExerciseSubmitterServerDataCollector(Logger logger, ExerciseSubmitterServerConfig configuration, Path workdir) {
        super("http+exercise-submitter-server");
        this.logger = logger;
        this.configuration = configuration;
        this.workdir = workdir;
        
        this.submissionServerClient = new ApiClient();
        this.submissionServerClient.setBasePath(configuration.getUrl());
        
        this.stuMgmtClient = new net.ssehub.studentmgmt.backend_api.ApiClient();
        this.stuMgmtClient.setBasePath(configuration.getStuMgmtUrl());
        
        net.ssehub.studentmgmt.sparkyservice_api.ApiClient authApiClient = new net.ssehub.studentmgmt.sparkyservice_api.ApiClient();
        authApiClient.setBasePath(configuration.getAuthUrl());
        this.authApi = new AuthControllerApi(authApiClient);
    }

    private Path getTempPath(DataProcessingRequest request) {
        return this.workdir.resolve(Path.of("request_" + request.hashCode()));
    }
    
    private void authenticateApiClients() throws ApiException {
        if (this.currentTokenExpiration == null
                || this.currentTokenExpiration.isBefore(Instant.now().plus(1, ChronoUnit.HOURS))) {
            
            try {
                this.logger.info("Logging into " + authApi.getApiClient().getBasePath()
                        + " with username " + this.configuration.getUsername());
                
                AuthenticationInfoDto dto = authApi.authenticate(
                        new CredentialsDto().username(this.configuration.getUsername()).password(this.configuration.getPassword()));
                
                TokenDto token = dto.getToken();
                submissionServerClient.setAccessToken(token.getToken());
                stuMgmtClient.setAccessToken(token.getToken());
                
                currentTokenExpiration = LocalDateTime.from(TOKEN_EXPIRATION_PARSER.parse(token.getExpiration()))
                        .toInstant(ZoneOffset.UTC);
                
            } catch (net.ssehub.studentmgmt.sparkyservice_api.ApiException e) {
                throw new ApiException("Failed to authenticate as " + this.configuration.getUsername() + ": " + e.getMessage());
                
            } catch (DateTimeParseException e) {
                this.logger.warn("Can't parse token expiration: " + e.getParsedString(), e);
            }
        }
    }
    
    private Submission download(String courseId, String assignmentName, String groupOrUserName, SubmissionApi submissionApi, Path tempPath) {
        Path target = tempPath.resolve(courseId + "_" + assignmentName + "_" + groupOrUserName);
        
        try {
            List<FileDto> files = submissionApi.getLatest(courseId, assignmentName, groupOrUserName);
            
            for (FileDto file : files) {
                Path targetFile = target.resolve(Path.of(file.getPath()));
                if (!Files.isDirectory(targetFile.getParent())) {
                    Files.createDirectories(target.getParent());
                }
                
                Files.write(targetFile, Base64.getDecoder().decode(file.getContent()));
            }
            
            return new Submission(courseId, assignmentName, groupOrUserName, target);
            
        } catch (IOException e) {
            this.logger.error("Failed to write submission", e);
            return null;
        } catch (ApiException e) {
            this.logger.error("Failed to retrieve submission", e);
            return null;
        }
    }
    
    private void iterateCoursesAndDownloadSubmissions(DataProcessingRequest request, SubmissionCollection result)
            throws net.ssehub.studentmgmt.backend_api.ApiException {
        
        Path tempPath = getTempPath(request);
        
        CourseApi courseApi = new CourseApi(stuMgmtClient);
        AssignmentApi assignmentApi = new AssignmentApi(stuMgmtClient);
        CourseParticipantsApi participantsApi = new CourseParticipantsApi(stuMgmtClient);
        AssignmentRegistrationApi registrationApi = new AssignmentRegistrationApi(stuMgmtClient);
        SubmissionApi submissionApi = new SubmissionApi(submissionServerClient);

        for (CourseDto course : courseApi.getCourses(null, null, null, null, null)) {
            if (request.courseMatches(course.getId())) {
                
                for (AssignmentDto assignment : assignmentApi.getAssignmentsOfCourse(course.getId())) {
                    if (request.homeworkMatches(assignment.getName())) {
                        if (assignment.getCollaboration() == CollaborationEnum.SINGLE
                                || assignment.getCollaboration() == CollaborationEnum.GROUP_OR_SINGLE) {
                            
                            for (ParticipantDto participant : participantsApi.getUsersOfCourse(course.getId(), null, null, null, null, null)) {
                                if (request.submissionMatches(participant.getUsername())) {
                                    Submission s = download(course.getId(), assignment.getName(), participant.getUsername(),
                                            submissionApi, tempPath);
                                    if (s != null) {
                                        result.add(s);
                                    }
                                }
                            }
                        }
                        
                        if (assignment.getCollaboration() == CollaborationEnum.GROUP
                                || assignment.getCollaboration() == CollaborationEnum.GROUP_OR_SINGLE) {
                            
                            for (GroupDto group : registrationApi.getRegisteredGroups(course.getId(), assignment.getId(), null, null, null)) {
                                if (request.submissionMatches(group.getName())) {
                                    Submission s = download(course.getId(), assignment.getId(), group.getName(),
                                            submissionApi, tempPath);
                                    if (s != null) {
                                        result.add(s);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public SubmissionCollection collect(DataProcessingRequest request) {
        SubmissionCollection result = new SubmissionCollection();
        
        try {
            authenticateApiClients();
            iterateCoursesAndDownloadSubmissions(request, result);
            
        } catch (ApiException e) {
            this.logger.error("Failed to log in", e);
        } catch (net.ssehub.studentmgmt.backend_api.ApiException e) {
            this.logger.error("Failed to query student management system", e);
        }
        
        return result;
    }
    
    @Override
    public void cleanup(DataProcessingRequest request) {
        Path tempPath = getTempPath(request);
        try {
            deleteDirectory(tempPath);
        } catch (IOException e) {
            this.logger.error("Failed to delete temporary folder " + tempPath, e);
        }
    }
    
    /**
     * Deletes a directory with all content of it.
     * 
     * @param directory The folder to delete.
     * 
     * @throws IOException If deleting the directory fails.
     */
    public static void deleteDirectory(Path directory) throws IOException {
        if (!Files.isDirectory(directory)) {
            throw new IOException(directory + " is not a directory");
        }
        
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

}
