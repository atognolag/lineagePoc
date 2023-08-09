# Java Lineage Client Demo

```
:exclamation: This documentation is a work in progress
```

## Reference setup
Starting from right to left these are the lineage steps:
* A looker studio pro report
* A BQ view
* A BQ copy job from 4 different projects
* 4 BQ datasets
        
        1. Two plain BQ datasets
        2. A BQ dataset from a different project
        3. A biglake dataset from GCS
 
![Lineage demo setup](./docs/images/Lineage%20demo.png)