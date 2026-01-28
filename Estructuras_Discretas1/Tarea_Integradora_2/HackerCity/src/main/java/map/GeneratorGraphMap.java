package map;

import structure.Edge;
import structure.Graph;
import structure.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneratorGraphMap {
    private final List<Node> nodes;
    private final Random random;

    public GeneratorGraphMap() {
        nodes = new ArrayList<>();
        random = new Random();
        createNodes();
    }

    public List<Node> getNodes() {
        return nodes;
    }

    // Método principal para generar el grafo completo
    public Graph generateGraph() {
        Graph graph = new Graph();

        for (Node node : nodes) {
            graph.addNode(node);
        }

        createEdges(graph);
        return graph;
    }

    // Agrega todos los nodos al mapa
    private void createNodes() {
        nodes.add(new Node("Corner1", 71, 141));
        nodes.add(new Node("Corner2", 125, 138));
        nodes.add(new Node("Corner3", 80, 199));
        nodes.add(new Node("Corner4", 134, 198));
        nodes.add(new Node("Corner5", 185, 180));
        nodes.add(new Node("Corner6", 511, 194));
        nodes.add(new Node("Corner7", 1545, 196));
        nodes.add(new Node("Corner8", 502, 493));
        nodes.add(new Node("Corner9", 566, 487));
        nodes.add(new Node("Corner10", 622, 485));
        nodes.add(new Node("Corner11", 678, 491));
        nodes.add(new Node("Corner12", 725, 467));
        nodes.add(new Node("Corner13", 800, 478));
        nodes.add(new Node("Corner14", 486, 547));
        nodes.add(new Node("Corner15", 536, 562));
        nodes.add(new Node("Corner16", 600, 566));
        nodes.add(new Node("Corner17", 663, 564));
        nodes.add(new Node("Corner18", 724, 598));
        nodes.add(new Node("Corner19", 808, 569));
        nodes.add(new Node("Corner20", 1208, 488));
        nodes.add(new Node("Corner21", 1287, 518));
        nodes.add(new Node("Corner22", 1141, 557));
        nodes.add(new Node("Corner23", 1191, 599));
        nodes.add(new Node("Corner24", 1242, 568));
        nodes.add(new Node("Corner25", 1287, 590));
        nodes.add(new Node("Corner26", 2128, 490));
        nodes.add(new Node("Corner27", 2190, 490));
        nodes.add(new Node("Corner28", 2253, 521));
        nodes.add(new Node("Corner29", 2102, 600));
        nodes.add(new Node("Corner30", 2151, 600));
        nodes.add(new Node("Corner31", 2202, 600));
        nodes.add(new Node("Corner32", 2238, 583));
        nodes.add(new Node("Corner33", 1127, 993));
        nodes.add(new Node("Corner34", 1177, 979));
        nodes.add(new Node("Corner35", 1272, 1002));
        nodes.add(new Node("Corner36", 1115, 1086));
        nodes.add(new Node("Corner37", 1173, 1085));
        nodes.add(new Node("Corner38", 1243, 1068));
        nodes.add(new Node("Corner39", 1280, 1068));
        nodes.add(new Node("Corner40", 1507, 1006));
        nodes.add(new Node("Corner41", 1613, 1008));
        nodes.add(new Node("Corner42", 1702, 1047));
        nodes.add(new Node("Corner43", 1752, 1057));
        nodes.add(new Node("Corner44", 1455, 1064));
        nodes.add(new Node("Corner45", 1508, 1068));
        nodes.add(new Node("Corner46", 1623, 1071));
        nodes.add(new Node("Corner47", 1502, 1122));
        nodes.add(new Node("Corner48", 1692, 1127));
        nodes.add(new Node("Corner49", 2137, 842));
        nodes.add(new Node("Corner50", 2232, 888));
        nodes.add(new Node("Corner51", 2151, 942));
        nodes.add(new Node("Corner52", 2197, 1009));
        nodes.add(new Node("Corner53", 2246, 999));
        nodes.add(new Node("Corner54", 2134, 1095));
        nodes.add(new Node("Corner55", 2191, 1131));
        nodes.add(new Node("Corner56", 545, 1520));
        nodes.add(new Node("Corner57", 694, 1419));
        nodes.add(new Node("Corner58", 691, 1484));
        nodes.add(new Node("Corner59", 676, 1545));
        nodes.add(new Node("Corner60", 760, 1476));
        nodes.add(new Node("Corner61", 814, 1416));
        nodes.add(new Node("Corner62", 752, 1547));
        nodes.add(new Node("Corner63", 848, 1483));
        nodes.add(new Node("Corner64", 831, 1538));
        nodes.add(new Node("Corner65", 1048, 1417));
        nodes.add(new Node("Corner66", 1111, 1409));
        nodes.add(new Node("Corner67", 1211, 1477));
        nodes.add(new Node("Corner68", 1065, 1528));
        nodes.add(new Node("Corner69", 1127, 1521));
        nodes.add(new Node("Corner70", 1195, 1544));
        nodes.add(new Node("Corner71", 1254, 1547));
        nodes.add(new Node("Corner72", 1533, 1418));
        nodes.add(new Node("Corner73", 1532, 1483));
        nodes.add(new Node("Corner74", 1734, 1481));
        nodes.add(new Node("Corner75", 1886, 1409));
        nodes.add(new Node("Corner76", 1973, 1418));
        nodes.add(new Node("Corner77", 1946, 1474));
        nodes.add(new Node("Corner78", 128, 1379));
        nodes.add(new Node("Corner79", 1007, 1054));
        nodes.add(new Node("Corner80", 1444, 176));
    }

    // Establece conexiones con niveles de seguridad aleatorios
    public void createEdges(Graph graph) {
        // Primer bloque
        connectBidirectional(graph, "Corner1", "Corner2");
        connectBidirectional(graph, "Corner2", "Corner4");
        connectBidirectional(graph, "Corner1", "Corner3");
        connectBidirectional(graph, "Corner3", "Corner4");
        connectBidirectional(graph, "Corner4", "Corner5");
        connectBidirectional(graph, "Corner5", "Corner6");
        connectBidirectional(graph, "Corner6", "Corner7");

        // Conexión desde Corner6 a bloque intermedio
        connectBidirectional(graph, "Corner6", "Corner8");
        connectBidirectional(graph, "Corner8", "Corner9");
        connectBidirectional(graph, "Corner9", "Corner10");
        connectBidirectional(graph, "Corner10", "Corner11");
        connectBidirectional(graph, "Corner11", "Corner12");
        connectBidirectional(graph, "Corner12", "Corner13");

        // Bifurcación hacia abajo
        connectBidirectional(graph, "Corner13", "Corner19");
        connectBidirectional(graph, "Corner19", "Corner18");
        connectBidirectional(graph, "Corner19", "Corner17");
        connectBidirectional(graph, "Corner17", "Corner16");
        connectBidirectional(graph, "Corner16", "Corner15");
        connectBidirectional(graph, "Corner15", "Corner14");

        // Conexión con zona media-derecha
        connectBidirectional(graph, "Corner19", "Corner20");
        connectBidirectional(graph, "Corner20", "Corner21");
        connectBidirectional(graph, "Corner21", "Corner22");
        connectBidirectional(graph, "Corner22", "Corner23");
        connectBidirectional(graph, "Corner23", "Corner24");
        connectBidirectional(graph, "Corner24", "Corner25");

        // Camino central largo
        connectBidirectional(graph, "Corner25", "Corner26");
        connectBidirectional(graph, "Corner26", "Corner27");
        connectBidirectional(graph, "Corner27", "Corner28");
        connectBidirectional(graph, "Corner28", "Corner29");
        connectBidirectional(graph, "Corner29", "Corner30");
        connectBidirectional(graph, "Corner30", "Corner31");
        connectBidirectional(graph, "Corner31", "Corner32");

        // Camino hacia zona baja
        connectBidirectional(graph, "Corner18", "Corner33");
        connectBidirectional(graph, "Corner33", "Corner34");
        connectBidirectional(graph, "Corner34", "Corner35");
        connectBidirectional(graph, "Corner35", "Corner36");
        connectBidirectional(graph, "Corner36", "Corner37");
        connectBidirectional(graph, "Corner37", "Corner38");
        connectBidirectional(graph, "Corner38", "Corner39");
        connectBidirectional(graph, "Corner39", "Corner40");
        connectBidirectional(graph, "Corner40", "Corner41");

        // Más conexiones en la zona baja
        connectBidirectional(graph, "Corner41", "Corner42");
        connectBidirectional(graph, "Corner42", "Corner43");
        connectBidirectional(graph, "Corner43", "Corner44");
        connectBidirectional(graph, "Corner44", "Corner45");
        connectBidirectional(graph, "Corner45", "Corner46");
        connectBidirectional(graph, "Corner46", "Corner47");
        connectBidirectional(graph, "Corner47", "Corner48");

        // Conexión con bloque 50+
        connectBidirectional(graph, "Corner48", "Corner49");
        connectBidirectional(graph, "Corner49", "Corner50");
        connectBidirectional(graph, "Corner50", "Corner51");
        connectBidirectional(graph, "Corner51", "Corner52");
        connectBidirectional(graph, "Corner52", "Corner53");
        connectBidirectional(graph, "Corner53", "Corner54");
        connectBidirectional(graph, "Corner54", "Corner55");

        // Última sección: zona inferior izquierda
        connectBidirectional(graph, "Corner55", "Corner56");
        connectBidirectional(graph, "Corner56", "Corner57");
        connectBidirectional(graph, "Corner57", "Corner58");
        connectBidirectional(graph, "Corner58", "Corner59");
        connectBidirectional(graph, "Corner58", "Corner60");
        connectBidirectional(graph, "Corner60", "Corner61");
        connectBidirectional(graph, "Corner60", "Corner62");
        connectBidirectional(graph, "Corner62", "Corner63");
        connectBidirectional(graph, "Corner63", "Corner64");

        // Cierre con nodo 65–80
        connectBidirectional(graph, "Corner64", "Corner65");
        connectBidirectional(graph, "Corner65", "Corner66");
        connectBidirectional(graph, "Corner66", "Corner67");
        connectBidirectional(graph, "Corner67", "Corner68");
        connectBidirectional(graph, "Corner68", "Corner69");
        connectBidirectional(graph, "Corner69", "Corner70");
        connectBidirectional(graph, "Corner70", "Corner71");
        connectBidirectional(graph, "Corner71", "Corner72");
        connectBidirectional(graph, "Corner72", "Corner73");
        connectBidirectional(graph, "Corner73", "Corner74");
        connectBidirectional(graph, "Corner74", "Corner75");
        connectBidirectional(graph, "Corner75", "Corner76");
        connectBidirectional(graph, "Corner76", "Corner77");
        //Objetivos con valores de seguridad mayores
        connectBidirectionalCustomSecurity(graph, "Corner77", "Corner78", 40);
        connectBidirectionalCustomSecurity(graph, "Corner78", "Corner79", 30);
        connectBidirectionalCustomSecurity(graph, "Corner79", "Corner80", 20);

        //conexiones dinamicas
        connectBidirectional(graph, "Corner8", "Corner14");
        connectBidirectional(graph, "Corner4", "Corner8");
        connectBidirectional(graph, "Corner7", "Corner77");
        connectBidirectional(graph, "Corner57", "Corner18");
        connectBidirectional(graph, "Corner31", "Corner75");
        connectBidirectional(graph, "Corner33", "Corner36");
        connectBidirectional(graph, "Corner48", "Corner67");
        connectBidirectional(graph, "Corner49", "Corner54");
        connectBidirectionalCustomSecurity(graph, "Corner54", "Corner78", 40);
        connectBidirectionalCustomSecurity(graph, "Corner32", "Corner79", 60);

    }

    // Conecta con niveles de seguridad específicos (para aristas especiales)
    private void connectBidirectionalCustomSecurity(Graph graph, String nodeId1, String nodeId2, int securityLevel) {
        Node node1 = getNodeByName(nodeId1);
        Node node2 = getNodeByName(nodeId2);

        if (node1 != null && node2 != null) {
            graph.addEdge(new Edge(node1, node2, securityLevel, false));
            graph.addEdge(new Edge(node2, node1, securityLevel, false));
        }
    }

    // Encuentra un nodo por ID
    private Node getNodeByName(String name) {
        for (Node node : nodes) {
            if (node.getId().equals(name)) {
                return node;
            }
        }
        return null;
    }

    // Crea una arista en ambas direcciones con seguridad aleatoria
    private void connectBidirectional(Graph graph, String nodeId1, String nodeId2) {
        Node node1 = getNodeByName(nodeId1);
        Node node2 = getNodeByName(nodeId2);

        if (node1 != null && node2 != null) {
            int securityLevel = generateRandomSecurityLevel();
            graph.addEdge(new Edge(node1, node2, securityLevel, false));
            graph.addEdge(new Edge(node2, node1, securityLevel, false));
        }
    }

    // Devuelve un valor entre 1 y 10
    private int generateRandomSecurityLevel() {
        return random.nextInt(10) + 1;
    }
}