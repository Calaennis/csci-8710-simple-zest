package project.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.zest.core.widgets.Graph;

import org.eclipse.swt.SWT;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import project.util.UtilFile;

public class SimpleZestView {
	private Graph graph;
	private int layout = 1;
	@Inject
	EPartService service;

	GraphNode node1 = null, node2 = null, node3 = null, node4 = null;
	List<GraphNode> graphNodes = new ArrayList<>();
	List<GraphConnection> graphConnections = new ArrayList<>();
	private static final String FILE_LOCATION = "input-data-zest-1.txt";

	public SimpleZestView() {
	}

	/**
	 * Create contents of the view part.
	 */
	@PostConstruct
	public void createControls(Composite parent) {
		graph = new Graph(parent, SWT.NONE); // Graph will hold all other objects
		updateGraphFromFile();
		addSelectionListener();
	}

	private void addSelectionListener() {
		// Selection listener on graphConnect
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<?> list = ((Graph) e.widget).getSelection();
				for (Object o : list) {
					if (o instanceof GraphNode) {
						GraphNode iNode = (GraphNode) o;
						findUpdateSimpleView(iNode);
					}
				}
			}
		};
		graph.addSelectionListener(selectionAdapter);
	}

	void findUpdateSimpleView(Item iNode) {
		// Find a view.
		MPart findPart = service.findPart(SimpleView.ID);
		SimpleView simpleView = (SimpleView) findPart.getObject();
		simpleView.appendText(iNode.getText() + "\n");
	}

	public void updateGraphFromFile() {
		clear();
		List<String> connections = getGraphConnections();
		List<String> uniqueNodes = getUniqueNodes(connections);
		createNodes(uniqueNodes);
		createConnections(connections);
		graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
	}

	private void clear() {
		for (GraphNode node : graphNodes) {
			node.dispose();
		}
	}

	private void createConnections(List<String> connections) {
		graphConnections = new ArrayList<>();
		for (String connection : connections) {
			System.out.println(connection);
			String[] nodesArray = connection.split(",");
			GraphNode firstNode = graphNodes.stream().filter(node -> ((String) node.getText()).equals(nodesArray[0]))
					.findFirst().get();
			GraphNode secondNode = graphNodes.stream().filter(node -> ((String) node.getText()).equals(nodesArray[1]))
					.findFirst().get();
			graphConnections.add(new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, firstNode, secondNode));
		}
	}

	private void createNodes(List<String> uniqueNodes) {
		graphNodes = new ArrayList<>(uniqueNodes.size());
		for (String node : uniqueNodes) {
			graphNodes.add(new GraphNode(graph, SWT.NONE, node));
		}
	}

	private List<String> getUniqueNodes(List<String> connections) {
		Set<String> uniqueNodes = new HashSet<>();
		connections.stream().map(connection -> connection.split(",")).forEach(nodeArray -> {
			for (String node : nodeArray) {
				uniqueNodes.add(node);
			}
		});
		return new ArrayList<>(uniqueNodes);
	}

	private List<String> getGraphConnections() {
		Bundle bundle = FrameworkUtil.getBundle(this.getClass());
		URL url = FileLocator.find(bundle, new Path(FILE_LOCATION), null);
		BufferedReader reader;
		try {
			File numbersFile = new File(FileLocator.toFileURL(url).toURI());
			reader = new BufferedReader(new FileReader(numbersFile));
			List<String> connections = reader.lines().collect(Collectors.toList());
			reader.close();
			return connections;
		} catch (URISyntaxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ArrayList<>();
	}

	@PreDestroy
	public void dispose() {
	}

	public void setLayoutManager() {
		switch (layout) {
		case 1:
			graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			layout++;
			break;
		case 2:
			graph.setLayoutAlgorithm(new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			layout = 1;
			break;
		}
	}

	@Focus
	public void setFocus() {
		this.graph.setFocus();
	}
}
