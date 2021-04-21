<p align="center">
  <a href="https://github.com/Student-Management-System/jacat">
    <img src="images/logo.png" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">JaCAT - Just another Code Analyzing Tool &nbsp;&nbsp;<img src="https://jenkins-2.sse.uni-hildesheim.de/buildStatus/icon?job=Teaching_JaCAT&style=flat-square" alt="Build Status"></h3> 

</p>



<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary><h2 style="display: inline-block">Table of Contents</h2></summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#TechStack">TechStack</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

The goal of this project is to perform various analyses regarding student submissions 
in a structured manner. It does this by providing an analysis infrastructure, 
which can also be extended by addons. This project already includes several of these 
addons. Among them is a DataCollector addon for SVN repositories, a plagiarism detection
addon that builds on [JPlag](http://jplag.ipd.kit.edu), and an analysis addon that analyzes students' code and provides
them with assistance. 

### TechStack

JaCAT is built on the following frameworks/toolkits:

* [Spring Boot](https://spring.io)
* [JPlag](http://jplag.ipd.kit.edu)
* [SVNKit](https://svnkit.com/)


<!-- GETTING STARTED -->
## Getting Started

To get a local copy up and running follow these simple steps.

### Prerequisites

### Installation

## Available Addons

### JPPlag

This addon can compare submissions and determine if certain things within the submission 
are similar. Usage and installation is documented under: 
[JPPlag](https://github.com/Student-Management-System/jacat/tree/main/jpplag-addon)

### PA-ResultProcessor

This addon post-processes the result of an analysis and converts similarities into 
'PartialAssessments'.  These are made available to the Student-Management-System and
automatically attached to an assessment.

<!-- USAGE EXAMPLES -->
## Usage

<!-- LICENSE -->
## License

See [LICENSE](https://github.com/Student-Management-System/jacat/blob/main/LICENSE) for more information.

<!-- CONTACT -->
## Contact

Project Link: [https://github.com/Student-Management-System/jacat](https://github.com/Student-Management-System/jacat)
