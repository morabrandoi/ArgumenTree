package com.example.argumentree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.argumentree.models.Question;
import com.example.argumentree.models.Response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import de.blox.graphview.Graph;
import de.blox.graphview.GraphAdapter;
import de.blox.graphview.GraphView;
import de.blox.graphview.Node;
import de.blox.graphview.tree.BuchheimWalkerAlgorithm;
import de.blox.graphview.tree.BuchheimWalkerConfiguration;


public class TreeActivity extends AppCompatActivity {
    public static final String TAG = "TreeActivity";

//    private Node currentNode;
    // UI Variables
    protected GraphView graphView;
    protected GraphAdapter adapter;

    // Graph variables
    private ArrayList<Response> allResponses;
    private Question question;
    private Node questionNode;
    private Graph graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree);

        // Pull Question from intent
        Intent intent = getIntent();
        Parcelable wrappedQuestion = intent.getParcelableExtra(Constants.QUESTION);
        question = Parcels.unwrap(wrappedQuestion);

        // This prevents a bug/crash where the layout dimension isn't set (I think)
        allResponses = new ArrayList<>();
        graph = new Graph();
        questionNode = new Node(question);
        graph.addNode(questionNode);
        setupAdapter(graph);

//        constructTree(new ArrayList<Response>());

        queryAndConstructTree();

    }

    private void queryAndConstructTree() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.FB_POSTS)
                .whereEqualTo( Constants.RESPONSE_QUESTION_REF, question.getDocID() )
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                adapter.notifyDataSetChanged();

                if (task.isSuccessful()){
                    Log.i(TAG, "task is successful");
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        Response response = document.toObject(Response.class);
                        response.setDocID(document.getId());

                        allResponses.add(response);
                        Log.i(TAG, "one doc received: " + response.getClaim());
                    }
                    constructTree();
                }
                else {
                    Snackbar.make(graphView, "Error " + task.getException(), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void constructTree() {
        // THE EXISTING EDGE MUST BE PUT FIRST
//        graph.addEdge(questionNode, );

        Queue<Node> queue = new LinkedList<Node>();
        // Attach the first layer to the question root
        for (Response response : allResponses){
            if (response.getParentRef().equals( question.getDocID() )){
                Node resNode = new Node(response);
                queue.add(resNode);
                graph.addNode(resNode);
                graph.addEdge(questionNode, resNode);
            }
        }

        // main
        while (!queue.isEmpty()){
            Log.i(TAG, "queue size: " + queue.size());
            Node connectTo = queue.remove();
            Response connectToResponse =  ( (Response) connectTo.getData() );

            for (Response response: allResponses){
                Log.i(TAG, "response Parent : " + response.getBrief() + ", connectDocID: " + connectToResponse.getBrief());
                if (response.getParentRef().equals(connectToResponse.getDocID()) ){
                    Node curNode = new Node(response);
                    queue.add(curNode);
                    graph.addNode(curNode);
                    graph.addEdge(connectTo, curNode);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void setupAdapter(final Graph graph) {
        graphView = findViewById(R.id.graph);
        // set the algorithm here
        final BuchheimWalkerConfiguration configuration = new BuchheimWalkerConfiguration.Builder()
                .setSiblingSeparation(30)
                .setLevelSeparation(200)
                .setSubtreeSeparation(200)
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
                Node curNode = graph.getNodeAtPosition(position);
                Object nodePost = curNode.getData();

                if (curNode.getData() instanceof Question){
                    Log.i(TAG, "binding question: " + question.getBody());
                    ((SimpleViewHolder) viewHolder).nodeText.setText(question.getBody());
                }
                else if (curNode.getData() instanceof Response){

                    Response response = (Response) nodePost;
                    ((SimpleViewHolder) viewHolder).nodeText.setText(response.getBrief());
                }
                else {
                    ((SimpleViewHolder) viewHolder).nodeText.setText("dummy Data");
                }
            }

            class SimpleViewHolder extends GraphView.ViewHolder {
                TextView nodeText;

                SimpleViewHolder(View itemView) {
                    super(itemView);
                    nodeText = itemView.findViewById(R.id.nodeText);
                }
            }
        };
        graphView.setAdapter(adapter);
        graphView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Node currentNode = (Node) adapter.getItem(position);
                Snackbar.make(graphView, "Clicked on " + currentNode.getData().toString(), Snackbar.LENGTH_SHORT).show();
            }
        });



    }
}