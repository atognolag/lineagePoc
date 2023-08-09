package org.example;

import com.google.api.core.ApiFuture;
import com.google.cloud.datacatalog.lineage.v1.*;
import com.google.api.core.ApiFutures;
import org.checkerframework.checker.units.qual.A;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;


public class NodeDependencies {
    private String project;
    private String location;
    private String rootUuid;
    private int maxDepth;
    private boolean upstream;
    private EntityReference rootReference;
    private Set<EntityReference> setDependencies;
    private List<EntityReference> listDependencies;
    private LineageClient client;

    public static class NodeDependenciesBuilder {
        private String project = "";
        private String location = "";
        private String nodeUuid = "";
        private int maxDepth = 25;
        private boolean upstream = false;

        public NodeDependenciesBuilder() {}
        public NodeDependenciesBuilder project(String project) {
            this.project = project;
            return this;
        }
        public NodeDependenciesBuilder location(String location) {
            this.location = location;
            return this;
        }
        public NodeDependenciesBuilder reportUuid(String nodeUuid) {
            this.nodeUuid = nodeUuid;
            return this;
        }
        public NodeDependenciesBuilder maxRecursionDepth(int maxDepth) {
            this.maxDepth = maxDepth;
            return this;
        }
        public NodeDependenciesBuilder setUpstream(boolean upstream) {
            this.upstream = upstream;
            return this;
        }
        public NodeDependencies build() throws Exception {
            if (this.project == "" || this.location == "" || this.nodeUuid == "") {
                throw new Exception("User error");
            }
            return new NodeDependencies(this.project, this.location, this.nodeUuid, this.maxDepth, this.upstream);
        }
    }

    NodeDependencies(String project, String location, String rootUuid, int maxDepth, boolean upstream) {
        this.project = project;
        this.location = location;
        this.rootUuid = rootUuid;
        this.rootReference = EntityReference.newBuilder()
                .setFullyQualifiedName(rootUuid)
                .build();
        this.maxDepth = maxDepth;
        this.setDependencies = new HashSet<EntityReference>();
        this.listDependencies = new ArrayList<EntityReference>();
        this.upstream = upstream;
        try {
            this.client = LineageClient.create();
            this.buildDeps();
        }
        catch(Exception e) {
            System.err.println(e);
        } finally {
            this.client.close();
        }
    }

    private void buildDeps() throws Exception {
        List<EntityReference> rootAsList = new ArrayList<EntityReference>();
        rootAsList.add(this.rootReference);
        this.iterateOverDeps(rootAsList, 0);
    }

    public List<EntityReference> getDeps() {
        return this.listDependencies;
    }

    public void iterateOverDeps(List<EntityReference> references, int currentDepth) throws Exception {
        if (currentDepth < this.maxDepth) {
            List<EntityReference> currNodeRefs = this.searchLinksBatched(references);
            List<EntityReference> nextNodeRefs = new ArrayList<EntityReference>();
            for (EntityReference node: currNodeRefs) {
                if (!this.setDependencies.contains(node)) {
                    this.setDependencies.add(node);
                    this.listDependencies.add(node);
                    nextNodeRefs.add(node);
                }
            }
            this.iterateOverDeps(nextNodeRefs, currentDepth+1);
        }
    }

    public List<EntityReference> searchLinksBatched(List<EntityReference> references) throws Exception {
        List<ApiFuture<SearchLinksResponse>> futures = new ArrayList<ApiFuture<SearchLinksResponse>>();
        for (EntityReference reference: references) {
            SearchLinksRequest request;
            if (!this.upstream) {
                request = SearchLinksRequest.newBuilder()
                                .setParent(LocationName.of(this.project,this.location).toString())
                                .setPageSize(1024)
                                .setTarget(reference)
                                .build();

            } else {
                request = SearchLinksRequest.newBuilder()
                        .setParent(LocationName.of(this.project,this.location).toString())
                        .setPageSize(1024)
                        .setSource(reference)
                        .build();
            }
            ApiFuture<SearchLinksResponse> future = this.client.searchLinksCallable().futureCall(request);
            futures.add(future);
        }

        List<EntityReference> outList = new ArrayList<EntityReference>();
        try {
            List<SearchLinksResponse> results = ApiFutures.allAsList(futures).get();
            for (SearchLinksResponse response: results) {
                //If needed, do something here with the full response data
                for (Link link: response.getLinksList()) {
                    //If needed, do something here with the full source
                    if (!this.upstream) {
                        outList.add(link.getSource());
                    } else {
                        outList.add(link.getTarget());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw (e);
        }
        return outList;
    }
}
