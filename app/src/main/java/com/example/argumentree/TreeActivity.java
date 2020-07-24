package com.example.argumentree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import de.blox.graphview.Graph;
import de.blox.graphview.GraphAdapter;
import de.blox.graphview.GraphView;
import de.blox.graphview.Node;
import de.blox.graphview.tree.BuchheimWalkerAlgorithm;
import de.blox.graphview.tree.BuchheimWalkerConfiguration;

public class TreeActivity extends AppCompatActivity {
    private Node currentNode;
    protected GraphView graphView;
    protected GraphAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree);

        final Graph graph = new Graph();
        final Node node1 = new Node("Parent");
        final Node node2 = new Node("Child 1");
        final Node node3 = new Node("Child 2");
        graph.addEdge(node1, node2);
        graph.addEdge(node1, node3);

        setupAdapter(graph);

        // you can set the graph via the constructor or use the adapter.setGraph(Graph) method


    }

    private void setupAdapter(final Graph graph) {
        graphView = findViewById(R.id.graph);
        // set the algorithm here
        final BuchheimWalkerConfiguration configuration = new BuchheimWalkerConfiguration.Builder()
                .setSiblingSeparation(100)
                .setLevelSeparation(300)
                .setSubtreeSeparation(300)
                .setOrientation(BuchheimWalkerConfiguration.ORIENTATION_TOP_BOTTOM)
                .build();
        graphView.setLayout(new BuchheimWalkerAlgorithm(configuration));
        adapter = new GraphAdapter<GraphView.ViewHolder>(graph) {

            @Override
            public int getCount() {
                return graph.getNodeCount();
            }

            @Override
            public Object getItem(int position) {
                return graph.getNodeAtPosition(position);
            }

            @Override
            public boolean isEmpty() {
                return graph.hasNodes();
            }

            @NonNull
            @Override
            public GraphView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.node, parent, false);
                return new SimpleViewHolder(view);
            }

            @Override
            public void onBindViewHolder(GraphView.ViewHolder viewHolder, Object data, int position) {
                ((SimpleViewHolder) viewHolder).textView.setText(data.toString());
            }

            class SimpleViewHolder extends GraphView.ViewHolder {
                TextView textView;

                SimpleViewHolder(View itemView) {
                    super(itemView);
                    textView = itemView.findViewById(R.id.nodeText);
                }
            }
        };
        graphView.setAdapter(adapter);
        graphView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentNode = (Node) adapter.getItem(position);
                Snackbar.make(graphView, "Clicked on " + currentNode.getData().toString(), Snackbar.LENGTH_SHORT).show();
            }
        });



    }
}