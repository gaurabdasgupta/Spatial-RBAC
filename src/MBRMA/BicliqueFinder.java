package MBRMA;

import javafx.util.Pair;

import java.util.*;

public class BicliqueFinder extends Biclique {
    private boolean foundAll = false;
    private int maxPossible;
    private BipartiteGraph graph;
    private VertexSet initL;
    private VertexSet initP;
    private VertexSet initR;
    private VertexSet initQ;
    private ArrayList<Biclique> maximalBicliques;
    private ArrayList<Biclique> MBC;
    private ArrayList<Biclique> roles;
    private HashSet<Pair<Integer,Integer>> vertexSet = new HashSet<>();


    public BicliqueFinder(BipartiteGraph inGraph)
    {
        graph = inGraph;
        initL = new VertexSet(graph.getLeftNodes());
        initP = new VertexSet(graph.getRightNodes());
        initR = new VertexSet();
        initQ = new VertexSet();
        maximalBicliques = new ArrayList<>();
        MBC = new ArrayList<>();
    }

    public void solve(String algType)
    {
        if(algType.equals("standard"))
        {
            bicliqueFind(initL, initR, initP, initQ);
            foundAll = true;
            this.toStringBicliqueF(maximalBicliques);
        }
        else if(algType.equals("roles"))
        {
            initP.sortByNumOfNeighbours();
            bicliqueFindimP(initL,initR,initP,initQ);
            foundAll=true;
            improvedFindNumberofRoles();
        }
    }

    public ArrayList<Biclique> getMaximalBicliques()
    {
        if(foundAll)
            return maximalBicliques;
        else
            System.out.println("not found yet");
        return null;
    }

    ArrayList<Biclique> getMBC() {
        return MBC;
    }

    public ArrayList<Biclique> getRoles() {
        return roles;
    }
    String getLRPQinit()
    {
        String res = null;
        for (int i = 0; i < initL.getSize(); i++){
            res += Integer.toString((initL.getVertex(i).getLabel())) + " ";
        }
        res += "\n";
        for (int i = 0; i < initR.getSize(); i++){
            res += Integer.toString((initR.getVertex(i).getLabel())) + " ";
        }
        res += "\n";
        for (int i = 0; i < initP.getSize(); i++){
            res += Integer.toString((initP.getVertex(i).getLabel())) + " ";
        }
        res += "\n";
        for (int i = 0; i < initQ.getSize(); i++){
            res += Integer.toString((initQ.getVertex(i).getLabel())) + " ";
        }
        res += "\n";

        return res;
    }

    private void bicliqueFind(VertexSet inL, VertexSet inR, VertexSet inP, VertexSet inQ)
    {
        VertexSet L = new VertexSet(inL.getSetV());
        VertexSet R = new VertexSet(inR.getSetV());
        VertexSet P = new VertexSet(inP.getSetV());
        VertexSet Q = new VertexSet(inQ.getSetV());
//        System.out.println("L: "+L.toStringVertexSet());
//        System.out.println("R: "+R.toStringVertexSet());
//        System.out.println("P: "+P.toStringVertexSet());
//        System.out.println("Q: "+Q.toStringVertexSet());

        while (!P.isSetEmpty())
        {
            Vertex x = P.getVertex(0);
//            System.out.println("x: "+x.getLabel());
            VertexSet Rprime = new VertexSet(R.getSetV());
//            System.out.println("R: "+R.toStringVertexSet());
            Rprime.addVertex(x);

            VertexSet Lprime = new VertexSet();

            for(int j=0;j<L.getSize();j++)
            {
                Vertex u = L.getVertex(j);
//                System.out.println("u: "+u.getLabel());
                if(u.isNeighbour(x))
                {
                    Lprime.addVertex(u);
//                    System.out.println("Lpr: "+Lprime.toStringVertexSet());
                }
            }

            VertexSet Pprime = new VertexSet();
            VertexSet Qprime = new VertexSet();

            boolean isMax = true;

            for(int j=0;j<Q.getSize();j++)
            {
                Vertex v = Q.getVertex(j);
//                System.out.println("v: "+v.getLabel());
                int numLprimeNeighbours = v.numberOfNeighboursOfVInSet(Lprime.getSetV());
//                System.out.println("numLprNeigh: "+numLprimeNeighbours);
                if(numLprimeNeighbours == Lprime.getSize())
                {
//                    System.out.println("inside if 1");
                    isMax = false;
                    break;
                }
                else if (numLprimeNeighbours > 0)
                {
                    Qprime.addVertex(v);
//                    System.out.println("inside else if 1");
//                    System.out.println("Qpr: "+Qprime.toStringVertexSet());
                }
            }

            if(isMax)
            {
//                System.out.println("inside if 2");
                for(int j=0;j<P.getSize();j++)
                {
                    Vertex v = P.getVertex(j);
//                    System.out.println("v: "+v.getLabel());
                    if(v.isEqual(x)) // doubt equals
                    {
//                        System.out.println("inside if 2.1");
                        continue;
                    }
//                    System.out.println("Lrp1: "+Lprime.toStringVertexSet());
                    int numLprimeNeighbours = v.numberOfNeighboursOfVInSet(Lprime.getSetV());
//                    System.out.println("numLprNeighs1: "+numLprimeNeighbours);
                    if(numLprimeNeighbours == Lprime.getSize())
                    {
                        Rprime.addVertex(v);
//                        System.out.println("Rpr1: "+Rprime.toStringVertexSet());
//                        System.out.println("debug R: "+R.hashCode()+" " +Rprime.hashCode());
                    }
                    else if(numLprimeNeighbours > 0)
                    {
                        Pprime.addVertex(v);
//                        System.out.println("Ppr: "+Pprime.toStringVertexSet());
                    }

                }

                Biclique bcq = new Biclique(Lprime.getSetV(), Rprime.getSetV());
                bcq.isMaximal = true;
                System.out.println(bcq.toStringBiclique());
                maximalBicliques.add(bcq);

                if(!Pprime.isSetEmpty()){
//                    System.out.println("calling again");
                    bicliqueFind(Lprime,Rprime,Pprime,Qprime);
                }


            }
            P.removeVertex(x);
//            System.out.println("P1: "+P.toStringVertexSet());
            Q.addVertex(x);
//            System.out.println("Q1: "+Q.toStringVertexSet());
//            System.out.println("last line debug R: "+Rprime.toStringVertexSet());
        }
    }

    private void bicliqueFindimP(VertexSet inL, VertexSet inR, VertexSet inP, VertexSet inQ)
    {
        VertexSet L = new VertexSet(inL.getSetV());
        VertexSet R = new VertexSet(inR.getSetV());
        VertexSet P = new VertexSet(inP.getSetV());
        VertexSet Q = new VertexSet(inQ.getSetV());

        while (!P.isSetEmpty())
        {
            Vertex x = P.getVertex(0);
            VertexSet Rprime = new VertexSet(R.getSetV());
            Rprime.addVertex(x);

            VertexSet Lprime = new VertexSet();
            VertexSet overlineLprime = new VertexSet(L.getSetV());
            VertexSet C = new VertexSet();

            for(int j=0;j<L.getSize();j++)
            {
                Vertex u = L.getVertex(j);
                if(u.isNeighbour(x))
                {
                    Lprime.addVertex(u);
                    overlineLprime.removeVertex(u);
                }
            }

            C.addVertex(x);

            VertexSet Pprime = new VertexSet();
            VertexSet Qprime = new VertexSet();

            isMaximal = true;

            for(int j=0;j<Q.getSize();j++)
            {
                Vertex v = Q.getVertex(j);
                int numLprimeNeighbours = v.numberOfNeighboursOfVInSet(Lprime.getSetV());

                if(numLprimeNeighbours == Lprime.getSize())
                {
                    isMaximal = false;
                    break;
                }
                else if (numLprimeNeighbours > 0)
                {
                    Qprime.addVertex(v);
                }
            }

            if(isMaximal)
            {
                for(int j=0;j<P.getSize();j++)
                {
                    Vertex v = P.getVertex(j);
                    if(v.isEqual(x)) // doubt equals
                        continue;

                    int numLprimeNeighbours = v.numberOfNeighboursOfVInSet(Lprime.getSetV());
                    if(numLprimeNeighbours == Lprime.getSize()) {
                        Rprime.addVertex(v);
                        int numoverlineLprimeneighbours = v.numberOfNeighboursOfVInSet(overlineLprime.getSetV());
                        if(numoverlineLprimeneighbours == 0)
                            C.addVertex(v);
                    }
                    else if(numLprimeNeighbours > 0)
                        Pprime.addVertex(v);
                }

                int isPresent = 0;
                Biclique bcq = new Biclique(Lprime.getSetV(), Rprime.getSetV());
                bcq.isMaximal = true;
//                System.out.println(bcq.toStringBiclique());
                for(Vertex v1:bcq.getLeftNodes())
                {
                    for(Vertex v2:bcq.getRightNodes())
                    {
                        Pair<Integer,Integer> pr = new Pair<>(v1.getLabel(),v2.getLabel());
                        if(vertexSet.contains(pr)){
                            isPresent++;
                        }

                        vertexSet.add(pr);
                    }

                }

                if(isPresent!=(bcq.getLeftNodes().size()*bcq.getRightNodes().size()))
                    maximalBicliques.add(bcq);

                if(!Pprime.isSetEmpty()){
                    bicliqueFindimP(Lprime,Rprime,Pprime,Qprime);
                }
            }

            for(int j=0;j<C.getSize();j++)
            {
                Vertex v = C.getVertex(j);
                Q.addVertex(v);
                P.removeVertex(v);
            }
        }
//        findMinimumBicliqueCover();
    }

    void findMinimumBicliqueCover()
    {
        VertexSet leftNodeList = new VertexSet(graph.getLeftNodes());
        List<Vertex> sortedLeftNodes = leftNodeList.sortByNumOfNeighbours();

        for(int i=0;i<sortedLeftNodes.size();i++)
        {
            Vertex v = sortedLeftNodes.get(i);
            for(int j=0;j<maximalBicliques.size();j++)
            {
                Biclique b = maximalBicliques.get(j);
//                System.out.println(b.toStringBiclique());
                List<Vertex> bLeft = b.getLeftNodes();
                boolean vertexPresent = bLeft.contains(v);
                if(vertexPresent)
                {
                    int present=0;
                    List<Vertex> bRight = b.getRightNodes();
                    for(Vertex right:v.getNeighbours())
                    {
                        if(bRight.contains(right))
                            present++;
                    }
                    if(present==v.getNeighboursSize())
                    {
                        MBC.add(b);
                        for(int k=0;k<bLeft.size();k++)
                        {
                            for(int l=0;l<bRight.size();l++)
                            {
                                bLeft.get(k).removeNeighbour(bRight.get(l));
                            }
                        }
//                        System.out.println("alsk");
//                        maximalBicliques.remove(b); // TODO: can add this line to remove duplicates, but all?
                        int abs=0;
                        for(Vertex v1:sortedLeftNodes)
                        {
                            if(v1.getNeighboursSize()==0)
                                abs++;
                        }
                        if(abs==sortedLeftNodes.size())
                            return;
                        break;
                    }
                }

            }
        }
    }

    boolean isAllNotCovered(VertexSet nodes)
    {
        int count=0;
        for(Vertex v: nodes.getSetV())
            if(v.getNeighboursSize()==0)
                count++;
        if(count==nodes.getSetV().size())
            return false;
        else
            return true;
    }

    void improvedFindNumberofRoles()
    {
        VertexSet leftNodeList = new VertexSet(graph.getLeftNodes());
        roles = new ArrayList<>();
        while(isAllNotCovered(leftNodeList))
        {
            Biclique candidate = null;
            int max = Integer.MIN_VALUE;
//            System.out.println(maximalBicliques.size());
            for(int i = 0; i < maximalBicliques.size(); i++)
            {
                Biclique b = maximalBicliques.get(i);
                List<Vertex> bLeft = b.getLeftNodes();
                List<Vertex> bRight = b.getRightNodes();
                int uncoveredCount = 0;
                for (Vertex v1 : bLeft) {
                    for (Vertex v2 : bRight) {
                        if (v1.isNeighbour(v2)){
                            uncoveredCount++;

                        }

                    }
                }
                if(uncoveredCount>max) {
                    max = uncoveredCount;
                    candidate = b;
                }
            }
            List<Vertex> canLeft = candidate.getLeftNodes();
            List<Vertex> canRight = candidate.getRightNodes();
            for(int k=0;k<canLeft.size();k++)
            {
                for(int l=0;l<canRight.size();l++)
                {
                    canLeft.get(k).removeNeighbour(canRight.get(l));
                }
            }
            roles.add(candidate);
            maximalBicliques.remove(candidate);
        }
    }


    int getNumBicliques()
    {
        if(foundAll)
            return maximalBicliques.size();
        return 0;
    }

    public String toStringBicliqueF(ArrayList<Biclique> b)
    {


        if(foundAll)
        {
            String res = "";
            for(int i=0;i<b.size();i++)
            {
                Biclique b1 = b.get(i);
                res += b1.toStringBiclique();
                res += "\n";
            }
            return res;
        }
        else
            return null;

    }
}
