package net.ssehub.jacat.addon.svndatacollector;

import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.diff.SVNDeltaProcessor;
import org.tmatesoft.svn.core.io.diff.SVNDiffWindow;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class SimpleCheckoutEditor implements ISVNEditor {

    private File baseDirectory;
    private SVNDeltaProcessor deltaProcessor;

    public SimpleCheckoutEditor(File root) {
        baseDirectory = root;
        deltaProcessor = new SVNDeltaProcessor();
    }

    public void targetRevision(long revision) throws SVNException {
    }

    public void openRoot(long revision) throws SVNException {
    }

    public void addDir(String path, String copyFromPath, long copyFromRevision) throws SVNException {
        File newDir = new File(baseDirectory, path);
        if (!newDir.exists()) {
            if (!newDir.mkdirs()) {
                SVNErrorMessage err = SVNErrorMessage
                        .create(SVNErrorCode.IO_ERROR, "error: failed to add the directory ''{0}''.", newDir);
                throw new SVNException(err);
            }
        }
    }

    public void openDir(String path, long revision) throws SVNException {
    }

    public void changeDirProperty(String name, SVNPropertyValue property) throws SVNException {
    }

    public void addFile(String path, String copyFromPath, long copyFromRevision) throws SVNException {
        File file = new File(baseDirectory, path);
        try {
            file.createNewFile();
        } catch (IOException e) {
            SVNErrorMessage err = SVNErrorMessage
                    .create(SVNErrorCode.IO_ERROR, "error: cannot create new  file ''{0}''", file);
            throw new SVNException(err);
        }
    }

    public void openFile(String path, long revision) throws SVNException {
    }

    public void changeFileProperty(String path, String name, SVNPropertyValue property) throws SVNException {
    }

    public void applyTextDelta(String path, String baseChecksum) throws SVNException {
        deltaProcessor.applyTextDelta((File) null, new File(baseDirectory, path), false);
    }

    public OutputStream textDeltaChunk(String path, SVNDiffWindow diffWindow) throws SVNException {
        return deltaProcessor.textDeltaChunk(diffWindow);
    }

    public void textDeltaEnd(String path) throws SVNException {
        deltaProcessor.textDeltaEnd();
    }

    public void closeFile(String path, String textChecksum) throws SVNException {
    }

    public void closeDir() throws SVNException {
    }

    public void deleteEntry(String path, long revision) throws SVNException {
    }

    public void absentDir(String path) throws SVNException {
    }

    public void absentFile(String path) throws SVNException {
    }

    public SVNCommitInfo closeEdit() throws SVNException {
        return null;
    }

    public void abortEdit() throws SVNException {
    }
}
