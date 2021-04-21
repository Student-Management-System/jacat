# PA-ResultProcessor

## About

In some cases it can be useful to let the platform post-process different results. 
For example if someone just wants to trigger a full analysis, this addon-type can 
persist different results as so called `PartialAssessments`. These are special 
notes which get attached to an assessment. 

## How to integrate

```bash 
│   jacat.jar
│
├───addons
│   │   pa-resultprocessor.jar
│
└───workspace
```

## Request Parameters

This addon currently supports the evaluation of request parameters, 
which can be sent along with an analysis request.  It can be important 
in a plagiarism analysis of source code submissions how sensitive 
the check is.  For this purpose, there are different options to set
when a submission should be considered as plagiarism.

|     Parameter Name     |  Possible Values  | Default | Description                                                               |
|------------------------|-------------------|---------|---------------------------------------------------------------------------|
| `similarityThreshold`  | `0.00` - `100.00` | `NONE`  | Sets the threshold below which legitimate submissions should be truncated |
| `classDeviation`       | `0` - `9`         | `NONE`  | Submissions whose similarity is below or within the average class + classDeviation will not be considered as plagiarism. |
| `paUpdateStrategy`     | `UPDATE`, `KEEP`  | `KEEP`  | Update: removes old PartialAssessments created before and adds new ones; Keep: Does not remove old PartialAssessments and adds new ones |

This addon won't add PartialAssessments if none of `similarityThreshold` or `classDeviation` is provided.

_Example_:

In the following example, submissions were evaluated on their (average)
similarity and divided into classes, the latter is done by this addon.  
It can be seen that on average the class '0 - 10%' is chosen.  The 
parameter `classDeviation` was set to 5. In the last column you can 
see that the classes 10 - 6 are considered as plagiarism.  If one would
use both parameters in connection, these would be linked with an OR.

| #  | Similarity Class | Submissions | Considered as Plagiarism? |
|----|------------------|-------------|---------------------------|
| 10 | 90 - 100%        | 1           | x                         |
| 9  | 80 - 90%         | 0           | x                         | 
| 8  | 70 - 80%         | 1           | x                         | 
| 7  | 60 - 70%         | 0           | x                         | 
| 6  | 50 - 60%         | 1           | x                         | 
| 5  | 40 - 50%         | 1           |                           | 
| 4  | 30 - 40%         | 5           |                           | 
| 3  | 20 - 30%         | 20          |                           | 
| 2  | 10 - 20%         | 400         |                           | 
| 1  |  0 - 10%         | 1200        |                           | 

## Related Addons

This addon can currently post-process the results of a similarity measurement. 
Therefore, it runs on the results of
[JPPlag](https://github.com/Student-Management-System/jacat/tree/main/jpplag-addon).

