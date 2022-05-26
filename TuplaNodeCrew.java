public class TuplaNodeCrew {

    private Node node;
    private Crew crew;

    public TuplaNodeCrew(Node node, Crew crew){
        this.crew = crew;
        this.node = node;
    }

    public Crew getCrew() {
        return crew;
    }

    public Node getNode() {
        return node;
    }
}
