# Java Lineage Client Demo

> :warning: **This documentation is a work in progress** and will be updated soon

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

## Sample output
```
/usr/bin/java [...] org.example.Main -u false -l us -p report-project -f datastudio:report.lsp-project.ec836f7c-b7d3-47f1-a715-3b725ffe51a8
[fully_qualified_name: "datastudio:datasource.lsp-project.8b96bfd2-da73-4c12-8cb8-47b343ecc10c"
, fully_qualified_name: "bigquery:report-project.data_lineage_demo.clean_trips_19-22"
, fully_qualified_name: "bigquery:report-project.data_lineage_demo.total_trips_19-22"
, fully_qualified_name: "bigquery:report-project.data_lineage_demo.nyc_green_trips_2019"
, fully_qualified_name: "bigquery:report-project.data_lineage_demo.nyc_green_trips_2022"
, fully_qualified_name: "bigquery:report-project.data_lineage_demo.nyc_green_trips_2021"
, fully_qualified_name: "bigquery:lsp-project.lineagetest2.nyc_green_trips_2020"
, fully_qualified_name: "gs://biglake_bucket/nyc_green_trips_2019/*.parquet"
, fully_qualified_name: "bigquery:bigquery-public-data.new_york_taxi_trips.tlc_green_trips_2019"
, fully_qualified_name: "bigquery:bigquery-public-data.new_york_taxi_trips.tlc_green_trips_2022"
, fully_qualified_name: "bigquery:bigquery-public-data.new_york_taxi_trips.tlc_green_trips_2021"
, fully_qualified_name: "bigquery:bigquery-public-data.new_york_taxi_trips.tlc_green_trips_2020"
]
```

## Getting started
### Quickstart
Browse to `./Java` and open up the project with Intellij, configure the `Run configurations` to include the appropriate command line parameters for your use case:
- *location*: The location where the Dataplex lineage data is located. Example: 'us' for US multi-region data
- *project*: The project id where the Dataplex lineage data is located.
- *upstream*: (optional) Sets the direction for lineage querying, upstream or not. Example: true or false.
- *fqdn*: The starting node's fqdn to report lineage. This can be either a Looker Studio Pro report, BQ datasets, Biglake/GCS files, etc.

        - For LSP reports, please use: 'datastudio:report.[GCP LSP project id].[Report uuid]'. Example: 'datastudio:report.my-lsp-project.ec836f7c-b7d3-47f1-a715-3b725ffe51a8'
        - For BQ datasets, please use: 'bigquery:[BQ project].[dataset name].[table name]'. Example: 'bigquery:my-bq-project.uber-cool-dataset.table'
        - For biglake or GCS, please use: 'gs://[bucket_id]/[full_path]'. Example: 'gs://my_gcs_bucket/nyc_green_trips_2019/*.parquet'