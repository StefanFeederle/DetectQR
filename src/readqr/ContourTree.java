package readqr;

import java.util.Enumeration;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Mat;

public class ContourTree {
	private DefaultMutableTreeNode root;
	private DefaultTreeModel model;
	private JTree tree;
	private Mat hierarchy;
	
	public ContourTree(){
	      root = new DefaultMutableTreeNode("Root");	      
	      model = new DefaultTreeModel(root);
	      tree = new JTree( model );  
	}
	public void buildtree(Mat hierarchy){
 	   for ( int i = 0; i < hierarchy.cols(); i++ ){
		   int iBuff1[] = new int[ (int) (4)];
		   hierarchy.get(0, i, iBuff1);		
		   System.out.println("ID "+i);
		   if(iBuff1[2]== -1 && iBuff1[3]== -1){
			   System.out.println("Child NO, Parent NO");
		   }
		   
		   else if(iBuff1[3]!= -1 && iBuff1[2]!= -1){	//Parent and a Child
			   System.out.println("Child YES, Parent YES");
			   //Parent
			   DefaultMutableTreeNode parent = find(root, Integer.toString(iBuff1[3]));
			   if(parent == null){
    			   parent = new DefaultMutableTreeNode( iBuff1[3] );
    			   DefaultMutableTreeNode child = new DefaultMutableTreeNode( i );
    			   parent.add(child);
    			   model.insertNodeInto(parent, root, 0); 
			   } else {
				   model.insertNodeInto(new DefaultMutableTreeNode( i ), parent, 0);   
			   }
			   
			   //Is the Child already in the Tree?
			   DefaultMutableTreeNode child = find(root, Integer.toString(iBuff1[2]));
			   if(child != null){
				   //Remove the Child and add it under the Parent
				   model.removeNodeFromParent(child); 
				   model.insertNodeInto(child, find(root, Integer.toString(i)), 0);
			   }  
		   }
		   
		   else if (iBuff1[3]!= -1){	//Only a Parent
			   System.out.println("Child NO, Parent YES");
			   //Parent
			   DefaultMutableTreeNode parent = find(root, Integer.toString(iBuff1[3]));
			   if(parent == null){
    			   parent = new DefaultMutableTreeNode( iBuff1[3] );
    			   DefaultMutableTreeNode child = new DefaultMutableTreeNode( i );
    			   parent.add(child);
    			   model.insertNodeInto(parent, root, 0); 
			   } else {
				   model.insertNodeInto(new DefaultMutableTreeNode( i ), parent, 0);   
			   }     
		   }
		   
		   else if(iBuff1[2]!= -1){	//Only a Child
			   System.out.println("Child YES, Parent NO");
			   model.insertNodeInto(new DefaultMutableTreeNode( i ), root, 0);
			   //Is the Child already in the Tree?
			   DefaultMutableTreeNode child = find(root, Integer.toString(iBuff1[2]));
			   if(child != null){
				   //Remove the Child and add it under the Parent
				   model.removeNodeFromParent(child); 
				   model.insertNodeInto(child, find(root, Integer.toString(i)), 0);
			   }    
		   }
		   
	   } 
		
	}
	private static DefaultMutableTreeNode find(DefaultMutableTreeNode root, String s) {
		@SuppressWarnings("unchecked")
		Enumeration<DefaultMutableTreeNode> e = root.depthFirstEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode node = e.nextElement();
			if (node.toString().equalsIgnoreCase(s)) {
				return node;
			}
		}
		return null;
	}
	void add(DefaultMutableTreeNode a) {
		this.add(a);	
	}
}
 