# JPPlag Plagiarismdetection Addon

## About

This addon can check assignments for plagiarism and uses the 
JPlag tool for this purpose.  For this purpose, it evaluates 
the result of the JPlag analysis and uses this information 
to generate data on the similarity of different assignments.

## How to integrate

If you want to add the analytics capability to the platform, 
all you have to do is place the jar file below the addons 
folder.  This is loaded when the platform is started and 
integrated into the process accordingly.  In addition, 'JPlag'
must be present as an executable jar file in the"addons/pp1plag"
folder.  At the moment, there is no way to configure, from 
where the addon should load the `jplag.jar`. 

The current version of JPlag can be obtained from: 
[JPlag Releases](https://github.com/jplag/jplag/releases)

```bash 
│   jacat.jar
│
├───addons
│   │   jpplag.jar
│   │
│   ├───pp1plag
│   │       jplag-2.12.1.jar
│
└───workspace
```

## Request Parameters

This addon currently does not support any evaluation of request parameters.

## Related Addons

This addon can work effortlessly with other addons. Currently, there
exists an addon that can further process the results of this analysis.
This analyzes the result and converts it into PartialAssessments. You
can find this Addon under 
[PA-ResultProcessor](https://github.com/Student-Management-System/jacat/tree/main/pa-resultprocessor)

## Analysis Result Format

Each analysis addon has the possibility to specify results in its own format. 
This can then be further processed by so-called result processors. This addon
calculates average similarities between source code submissions and outputs
them in the following format. For better understanding, the following is an
example of such an output: 

````json
{
  "similarities": [
    {
      "from": "jp01",
      "to": [
        {
          "submission": "jp02",
          "similarity": 87.74
        },
        {
          "submission": "jp03",
          "similarity": 80.43
        }
      ]
    },
    {
      "from": "jp03",
      "to": [
        {
          "submission": "jp02",
          "similarity": 91.0
        }
      ]
    }
  ]
}
````

Speaking of Java this structure is represented as a `Map<String, Object>` which is passed here. 
In this case  the value is a list of other objects (similarities), which can be easily converted 
into self managed objects. For this use-case one could try to serialize this structure into json 
and deserialize it afterwards back into a custom managed class. 


