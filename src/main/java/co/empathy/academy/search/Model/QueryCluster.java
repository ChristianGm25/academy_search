package co.empathy.academy.search.Model;

public class QueryCluster {
    String query;
    String cluster;

    public QueryCluster(String query, String cluster) {
        this.query = query;
        this.cluster = cluster;
    }
    public QueryCluster() {
        this.query = "";
        this.cluster = "";
    }
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }
}
