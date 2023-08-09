package org.example;

import org.apache.commons.cli.*;

public class Main {
  public static void main(String[] args) throws Exception {
    Options options = new Options();

    Option input1 = new Option("p", "project", true, "The project where the Dataplex lineage data is located");
    input1.setRequired(true);
    options.addOption(input1);

    Option input2 = new Option("l", "location", true, "The location where the Dataplex lineage data is located. Example: 'us' for US multi-region data");
    input2.setRequired(true);
    options.addOption(input2);

    Option input3 = new Option("f", "fqdn", true, "The report or lineage node's fqdn.\n" +
            "For LSP reports, please use: 'datastudio:report.[GCP LSP project id].[Report uuid]'.\nExample: 'datastudio:report.my-lsp-project.ec836f7c-b7d3-47f1-a715-3b725ffe51a8'.\n" +
            "For BQ datasets, please use: 'bigquery:[BQ project].[dataset name]'.\nExample: 'bigquery:my-bq-project.uber-cool-dataset'.\n" +
            "For biglake or GCS, please use: 'gs://[bucket_id]/[full_path]'.\nExample: 'gs://my_gcs_bucket/nyc_green_trips_2019/*.parquet'.\n");
    input3.setRequired(true);
    options.addOption(input3);

    Option input4 = new Option("u", "upstream", true, "The direction to query the lineage data. Example: 'true' for upstream dependencies, 'false' for downstream ones.");
    input4.setRequired(true);
    options.addOption(input4);

    CommandLineParser parser = new DefaultParser();
    HelpFormatter formatter = new HelpFormatter();
    CommandLine cmd = null;//not a good practice, it serves it purpose

    try {
      cmd = parser.parse(options, args);
    } catch (ParseException e) {
      System.out.println(e.getMessage());
      formatter.printHelp("Dataplex CLI lineage tool", options);

      System.exit(1);
    }

    String project = cmd.getOptionValue("p");
    String location = cmd.getOptionValue("l");
    String fqdn = cmd.getOptionValue("f");
    String upstreamString = cmd.getOptionValue("u");
    boolean upstream = false;
    if (upstreamString.equals("true")) {
      upstream = true;
    }

    NodeDependencies lineageDependencies = new NodeDependencies.NodeDependenciesBuilder()
            .project(project)
            .location(location)
            .setUpstream(upstream)
            .reportUuid(fqdn)
            .build();
    System.out.println(lineageDependencies.getDeps().toString());

    //A couple examples follow:
    /*NodeDependencies myTest1 = new NodeDependencies.NodeDependenciesBuilder()
            .project("gcp-dataplex-project")
            .location("us")
            .setUpstream(false)
            .reportUuid("datastudio:report.gcp-lsp-project.ec836f7c-b7d3-47f1-a715-3b725ffe51a8")
            .build();
    System.out.println(myTest1.getDeps().toString());*/
  }
}