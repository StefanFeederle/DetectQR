package readqr;

import java.awt.*;  
import java.awt.image.BufferedImage;  
import java.io.ByteArrayInputStream;  
import java.io.IOException;  
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.awt.geom.Line2D;

import javax.imageio.ImageIO;  
import javax.swing.*;  
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.opencv.core.Core;  
import org.opencv.core.CvType;
import org.opencv.core.Mat;  
import org.opencv.core.MatOfByte;  
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f; 
import org.opencv.core.Point;  
import org.opencv.core.Rect;   
import org.opencv.core.Scalar;  
import org.opencv.core.Size;  
import org.opencv.highgui.Highgui;  
import org.opencv.highgui.VideoCapture;  
import org.opencv.imgproc.Imgproc;  
import org.opencv.imgproc.Moments;

class FacePanel extends JPanel{  
     private static final long serialVersionUID = 1L;  
     private BufferedImage image;  
     // Create a constructor method  
     public FacePanel(){  
          super();   
     }  
     /*  
      * Converts/writes a Mat into a BufferedImage.  
      *   
      * @param matrix Mat of type CV_8UC3 or CV_8UC1  
      * @return BufferedImage of type TYPE_3BYTE_BGR or TYPE_BYTE_GRAY  
      */       
     public boolean matToBufferedImage(Mat matrix) {  
          MatOfByte mb=new MatOfByte();  
          Highgui.imencode(".jpg", matrix, mb);  
          try {  
               this.image = ImageIO.read(new ByteArrayInputStream(mb.toArray()));  
          } catch (IOException e) {  
               e.printStackTrace();  
               return false; // Error  
          }  
       return true; // Successful  
     }  
     public void paintComponent(Graphics g){  
          super.paintComponent(g);   
          if (this.image==null) return;         
           g.drawImage(this.image,0,0,this.image.getWidth(),this.image.getHeight(), null);
     }
        
}  

public class Main {  
	static Scalar blue = new Scalar(255.0,200.0,0.0);
	static Scalar red = new Scalar(0.0,0.0,255.0);
	static Scalar green = new Scalar(0.0,255.0,0.0);

	public static void main(String arg[]) throws InterruptedException{ 
      // Load the native library.  
      System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 
      //or ...     System.loadLibrary("opencv_java244");       

      //make the JFrame
      JFrame frame = new JFrame("WebCam Capture");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      FacePanel facePanel = new FacePanel();  
           
      frame.setSize(735,445); //give the frame some arbitrary size 
      frame.setBackground(Color.BLUE);
      frame.add(facePanel,BorderLayout.CENTER);      
      frame.setVisible(true); 
      
      JFrame framecanny = new JFrame("Canny Edge Detection");
      framecanny.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
      FacePanel facePanelcanny = new FacePanel();  
      framecanny.setSize(735,445); //give the frame some arbitrary size 
      framecanny.setLocation(735, 0);
      framecanny.setBackground(Color.BLUE);
      framecanny.add(facePanelcanny,BorderLayout.CENTER);   
      framecanny.setVisible(true); 
 
      JFrame frameslider = new JFrame("Canny Threshold 1");
      frameslider.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
      frameslider.setSize(735,100); //give the frame some arbitrary size 
      frameslider.setLocation(735, 445);
      frameslider.setBackground(Color.BLUE); 
      
      JFrame frameqr = new JFrame("QR");
      frameqr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   
      FacePanel facePanelqr = new FacePanel();  
      frameqr.setSize(416,440); //give the frame some arbitrary size 
      frameqr.setLocation(0, 445);
      frameqr.setBackground(Color.BLUE);
      frameqr.add(facePanelqr,BorderLayout.CENTER);  
      frameqr.setVisible(true);

      JFrame frametree = new JFrame( "JTree" );
      frametree.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      //frametree.pack();
           
      DefaultMutableTreeNode root = createTree();
      //DefaultMutableTreeNode root = new DefaultMutableTreeNode("R");
      
      try {
          UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
          e.printStackTrace();
        }
      
      DefaultTreeModel model = new DefaultTreeModel(root);
      final JTree tree = new JTree( model );  
      tree.setEditable(true);
      tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
      JScrollPane Paneltree = new JScrollPane(tree);
      frametree.add(Paneltree);     

      tree.addTreeSelectionListener(new TreeSelectionListener() {
    	    public void valueChanged(TreeSelectionEvent e) {
    	        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

    	    // if nothing is selected  
    	        if (node == null) return;

    	    // retrieve the node that was selected
    	        Object nodeInfo = node.getUserObject();
    	        //System.out.println(node);
    	        //drawcontour(node);
    	    // React to the node selection. 

    	    }
    	});
    	

      frametree.setSize(319,440);
      frametree.setLocation(416, 445);
      frametree.setVisible( true );
      
      //model.insertNodeInto(new DefaultMutableTreeNode( "1" ), find(root, "Root Node"), 0);
      DefaultMutableTreeNode node = find(root, "A");
      node.add(new DefaultMutableTreeNode( "11" ));
      model.insertNodeInto(new DefaultMutableTreeNode( "1" ), node, 0);
      model.reload();
      
      //Paneltree.add(tree);
      Paneltree.repaint();   
      
      
      // Slider Min Max Init
      JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 2500, 1500);
      slider.setMajorTickSpacing(100);
      slider.setMinorTickSpacing(25);
      slider.setPaintTicks(true);
      slider.setPaintLabels(true);
      frameslider.add(slider);
      frameslider.setVisible(true);  

      
      int debugtext = 1;
      
      //Open and Read from the video stream  
       Mat webcam_image=new Mat(); 
       Mat grayscale_image=new Mat(); 
       Mat canny_image=new Mat();
       Mat qr_image=new Mat(400,400, CvType.CV_8UC3, new Scalar(0,0,0));       

       List<MatOfPoint> cascades = new ArrayList<MatOfPoint>();
       List<List<MatOfPoint>> markers = new ArrayList<List<MatOfPoint>>();
       List<List<MatOfPoint>> alignmentmarkers = new ArrayList<List<MatOfPoint>>();
       List<List<MatOfPoint>> testMarkers = new ArrayList<List<MatOfPoint>>();
       List<Point> centersofmarkers = new ArrayList<Point>();
       List<MatOfPoint> dummy = new ArrayList<MatOfPoint>();
       List<MatOfPoint2f> cascades2f = new ArrayList<MatOfPoint2f>();
       List<MatOfPoint> cascadespoly = new ArrayList<MatOfPoint>();
       List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
       
       Mat hierarchy = new Mat();

       VideoCapture webCam = new VideoCapture(0);   
       webCam.open(0);
       //webCam.set(Highgui.CV_CAP_PROP_FRAME_WIDTH, 1280);
       //webCam.set(Highgui.CV_CAP_PROP_FRAME_HEIGHT, 720);
       webCam.set(Highgui.CV_CAP_PROP_FRAME_WIDTH, 960);
       webCam.set(Highgui.CV_CAP_PROP_FRAME_HEIGHT, 544);
       System.out.println("Stream is "+(int) webCam.get(Highgui.CV_CAP_PROP_FRAME_WIDTH)+"px "+(int) webCam.get(Highgui.CV_CAP_PROP_FRAME_HEIGHT)+"px");

       if( webCam.isOpened()){  
           Thread.sleep(500); /// This one-time delay allows the Webcam to initialize itself  
           while( true ){    
        	   long startTime = System.nanoTime();
        	   webCam.read(webcam_image);  
        	   if( !webcam_image.empty() ){ 
        		   long webcamTime = System.nanoTime();
        		   //Thread.sleep(500); /// This delay eases the computational load .. with little performance leakage
        		   //System.out.println("New Frame started");
            	   Imgproc.cvtColor(webcam_image, grayscale_image, Imgproc.COLOR_RGB2GRAY);
            	   Imgproc.GaussianBlur(grayscale_image, grayscale_image, new Size(5,5), 0);
            	   Imgproc.equalizeHist(canny_image, canny_image);
            	   Imgproc.Canny(grayscale_image, canny_image, slider.getValue(), slider.getValue(), 5, true); 
            	   Imgproc.findContours( canny_image, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
            	   //System.out.println(hierarchy.cols());

            	   //Contours Contours1 = new Contours(canny_image);
            	   //contours.clear();
            	   
            	   if (debugtext == 2){
            		   System.out.println(hierarchy.dump());
            		   int iBuff[] = new int[ (int) (4) ];
            		   //hierarchy.get(0, 4, iBuff);
            		   hierarchy.get(0, 0, iBuff);
            		   System.out.println(Arrays.toString(iBuff));
            		   System.out.println(iBuff[1]);
            		   System.out.println(hierarchy.size());
            		   System.out.println(contours.size());
            		   debugtext = 0;
            	   }  
            	   
            	   long TimeStartTree = System.nanoTime();
            	   
            	   ContourManager CM = new ContourManager(contours, hierarchy);

            	   //markers = findNestedContours(contours, hierarchy, 5);
            	   CM.buildNestedContours();
            	   markers = CM.getNestedContours(5);
            	   //System.out.println("found "+markers.size()+" contours");

            	   for ( int i = 0; i < contours.size(); i++ ){
            		   int hierarchyElement[] = new int[ (int) (4)];
            		   
            		   //the hierarchy array defines the relationships from one element to the tree. Four Numbers describe previous, next, parent and child element-ids. In this case only parent and child are relevant. 
            		   hierarchy.get(0, i, hierarchyElement);		
            		   //System.out.println("ID "+i);
            		   
            		   if(hierarchyElement[2]== -1 && hierarchyElement[3]== -1){
            			   //System.out.println("Child NO, Parent NO");
            		   }
            		   
            		   else if(hierarchyElement[3]!= -1 && hierarchyElement[2]!= -1){	//Parent and a Child
            			   //System.out.println("Child YES, Parent YES");
            			   //Parent
            			   DefaultMutableTreeNode parent = find(root, Integer.toString(hierarchyElement[3]));
            			   if(parent == null){
	            			   parent = new DefaultMutableTreeNode( hierarchyElement[3] );
	            			   DefaultMutableTreeNode child = new DefaultMutableTreeNode( i );
	            			   parent.add(child);
	            			   model.insertNodeInto(parent, root, 0); 
            			   } else {
            				   model.insertNodeInto(new DefaultMutableTreeNode( i ), parent, 0);   
            			   }
            			   
            			   //Is the Child already in the Tree?
        				   DefaultMutableTreeNode child = find(root, Integer.toString(hierarchyElement[2]));
        				   if(child != null){
        					   //Remove the Child and add it under the Parent
        					   model.removeNodeFromParent(child); 
        					   model.insertNodeInto(child, find(root, Integer.toString(i)), 0);
        				   }  
            		   }
            		   
            		   else if (hierarchyElement[3]!= -1){	//Only a Parent
            			   //System.out.println("Child NO, Parent YES");
            			   //Parent
            			   DefaultMutableTreeNode parent = find(root, Integer.toString(hierarchyElement[3]));
            			   if(parent == null){
	            			   parent = new DefaultMutableTreeNode( hierarchyElement[3] );
	            			   DefaultMutableTreeNode child = new DefaultMutableTreeNode( i );
	            			   parent.add(child);
	            			   model.insertNodeInto(parent, root, 0); 
            			   } else {
            				   model.insertNodeInto(new DefaultMutableTreeNode( i ), parent, 0);   
            			   }     
            		   }
            		   
            		   else if(hierarchyElement[2]!= -1){	//Only a Child
            			   //System.out.println("Child YES, Parent NO");
            			   model.insertNodeInto(new DefaultMutableTreeNode( i ), root, 0);
            			   //Is the Child already in the Tree?
        				   DefaultMutableTreeNode child = find(root, Integer.toString(hierarchyElement[2]));
        				   if(child != null){
        					   //Remove the Child and add it under the Parent
        					   model.removeNodeFromParent(child); 
        					   model.insertNodeInto(child, find(root, Integer.toString(i)), 0);
        				   }    
            		   }
            		   
            	   } 
            	   long TimeEndTree = System.nanoTime();  
            	   
            	   markers = dropmarkersbypoly(markers, 4, webcam_image, green);
            	   centersofmarkers = centerofmarkers(markers);
            	   
            	   if (markers.size() == 3){
            		   List<Point> orderedMarkers = orderPoints(centersofmarkers);

            		   Point top = 		orderedMarkers.get(0);
            		   Point right = 	orderedMarkers.get(1);
            		   Point left = 	orderedMarkers.get(2);
      		   
            		   int width = (int) Math.abs(distance(right, top));
            		   int height = (int) Math.abs(distance(left, top));
            		   //System.out.println("width is  " + width + " and height is "  + height);
              		   
            		   //Center of QR Code is mean between right and left
            		   Point center = new Point((left.x+right.x) / 2, (left.y+right.y) / 2);
            		   
            		   Point centerSearchRect = new Point(center.x + 0.7*(center.x - top.x), center.y + 0.7*(center.y - top.y));
              		
            		   //draw rect where fourth marker should be
            		   Point topleft = 	new Point(centerSearchRect.x-(width/4), centerSearchRect.y-(height/4));
            		   Point bottomright = new Point(centerSearchRect.x+(width/4), centerSearchRect.y+(height/4));             		    
            		   Core.rectangle(webcam_image, topleft, bottomright, red);
            		   Rect rect = new Rect(topleft, bottomright);
              		   
            		   alignmentmarkers = CM.getNestedContours(3);
            		   //Iterate over markerlist
            		   for ( int i = 0; i < alignmentmarkers.size(); i++ ){   
            			   List<MatOfPoint> currentMarker = alignmentmarkers.get(i);		//Get marker
            			   MatOfPoint biggestContour = currentMarker.get(0);				//Get biggest contour
            			   
            			   if(allPointsInRect(biggestContour, rect)){
            				   testMarkers.add(new ArrayList<MatOfPoint>(currentMarker));
            				   //Imgproc.drawContours( webcam_image, currentMarker, currentMarker.indexOf(currentMarker.get(0)), green, 1);
            				   //alignmentmarkers = dropmarkersbypoly(alignmentmarkers, 4, webcam_image, green);
            			   }else {
            				   //Imgproc.drawContours( webcam_image, currentMarker, currentMarker.indexOf(currentMarker.get(0)), red, 1);
            			   }
            				  
            		   	}
            		    testMarkers = dropmarkersbypoly(testMarkers, 4, webcam_image, green);
            		   	Point four = new Point();
            		   	if (testMarkers != null && !testMarkers.isEmpty()){
            		   		four = centerofmarkers(testMarkers).get(0);
			   			}
            		   	testMarkers.clear();
 	
                	
            		   	Core.circle(webcam_image,center, 4, new Scalar(0,255,0,255));
            		   	Core.circle(webcam_image,centerSearchRect, 4, new Scalar(255,0,0,255));   
            		   	drawlinesbetweenpoints(webcam_image, orderedMarkers); 
            		   	//Core.putText(webcam_image, "Slope: "+ ((slope > 0) ? "+" : "-"), new Point(20, 130), Core.FONT_HERSHEY_COMPLEX, 0.8, new Scalar(0,0,255), 2);
            		   	//Core.putText(webcam_image, "Dist:  "+ ((dist > 0) ? "+" : "-"), new Point(20, 160), Core.FONT_HERSHEY_COMPLEX, 0.8, new Scalar(0,0,255), 2);
    			
            		   	Core.putText(webcam_image, "Top", new Point(top.x+20, top.y+10), Core.FONT_HERSHEY_COMPLEX, 0.8, new Scalar(0,0,255), 2);
            		   	Core.putText(webcam_image, "Left", new Point(left.x+20, left.y+10), Core.FONT_HERSHEY_COMPLEX, 0.8, new Scalar(0,0,255), 2);
            		   	Core.putText(webcam_image, "Right", new Point(right.x+20, right.y+10), Core.FONT_HERSHEY_COMPLEX, 0.8, new Scalar(0,0,255), 2);  
         		   
         		   
            		   	MatOfPoint2f src = new MatOfPoint2f();
            		   	Point srcpoint[] = new Point[4];               	   
            		   	srcpoint[0] = top;
            		   	srcpoint[1] = right;
            		   	srcpoint[2] = left;
            		   	srcpoint[3] = four;       	
            		   	src.fromArray(srcpoint);
            		   
            		   	MatOfPoint2f dst = new MatOfPoint2f();            		    		   
            		   	Point dstpoint[] = new Point[4];           		   
            		   	dstpoint[0] = new Point (70,70);
            		   	dstpoint[1] = new Point (330,70);
            		   	dstpoint[2] = new Point (70,330);
            		   	dstpoint[3] = new Point (300,300);
            		   
            		   	dst.fromArray(dstpoint);
            		    
            		   	Mat PerspectiveMat = Imgproc.getPerspectiveTransform(src, dst);            		   
            		   	Imgproc.warpPerspective(webcam_image, qr_image, PerspectiveMat, new Size(400,400), Imgproc.INTER_CUBIC);
            	   }
            	   
            	   //for(int i = 0; i < markers.size(); i++){
            		   //System.out.println("Marker #"+i+" has "+markers.get(i).size()+" Polys");
            		   //Circle all Markers
                	   //Core.circle(webcam_image, centersofmarkers.get(i), 4, new Scalar(255,49,0,255));
                	   //Draw Markers Contours
            		   //Imgproc.drawContours( webcam_image, markers.get(i), -1, red, 2);
            		   //Draw Marker ID
            		   //Core.putText(webcam_image, String.valueOf(i), new Point(centersofmarkers.get(i).x-35, centersofmarkers.get(i).y+8), Core.FONT_HERSHEY_COMPLEX, 0.8, new Scalar(0,0,255), 2);
            	   //}
            	   
            	   //Debug: Show Marker Count and Contour Count
            	   //Core.putText(webcam_image, "Markers: "+ markers.size(), new Point(20, 40), Core.FONT_HERSHEY_COMPLEX, 1.5, red, 2);
            	   //Core.putText(webcam_image, "Contours: "+ contours.size() +" found", new Point(20, 100), Core.FONT_HERSHEY_COMPLEX, 1.5, red, 2);

            	   long drawTime = System.nanoTime();
            	   contours.clear();
            	   cascades.clear();
            	   cascadespoly.clear();
            	   markers.clear();
            	   alignmentmarkers.clear();
            	   
            	   Imgproc.resize(webcam_image, webcam_image, new Size(720, 405));
        		   facePanel.matToBufferedImage(webcam_image);  
        		   facePanel.repaint();   
        		   Imgproc.resize(canny_image, canny_image, new Size(720, 405));
        		   facePanelcanny.matToBufferedImage(canny_image);  
        		   facePanelcanny.repaint(); 
        		   facePanelqr.matToBufferedImage(qr_image);  
        		   facePanelqr.repaint(); 
        		   
   				long stopTime = System.nanoTime();
   				double elapsedTime = (stopTime - startTime)/1000000;
   				double elapsedTimewebcam = (webcamTime - startTime)/1000000;
   				double elapsedTimecompute = (drawTime - webcamTime)/1000000;
   				double elapsedTimetree = (TimeEndTree - TimeStartTree)/1000000;
   				double elapsedTimedraw = (stopTime - drawTime)/1000000;
   				System.out.println("Whole loop took "+elapsedTime+" ms. Read "+(int) (elapsedTimewebcam*100/elapsedTime)+" %. Compute "+(int) (elapsedTimecompute*100/elapsedTime) +" %. Tree "+(int) (elapsedTimetree*100/elapsedTime)+" %. Draw "+(int) (elapsedTimedraw*100/elapsedTime) +" %.");
        		   

              }else{   
            	  System.out.println(" --(!) No captured frame from webcam !");   
            	  //break;   
              }  
           }  
       }
       webCam.release(); //release the webcam
		} //end main 
	
	
	
		public static Point centerofcontour(MatOfPoint contour) {
			//Return Point(centerofmass) for one contour
			Moments p = Imgproc.moments(contour, false);
			int x = (int) (p.get_m10() / p.get_m00());
			int y = (int) (p.get_m01() / p.get_m00());
			return new Point(x, y);
		} 	
	
		public static Point centerofcontours(List<MatOfPoint> contours) {
			//Get avarage centerofmass for multiple contours
			//System.out.println("Center of Mass called once, the object contains "+cascades.size()+" contours.");
			//long startTime = System.nanoTime();
			List<Point> centers = new ArrayList<Point>(); 
			//Get centerofmass for each contour
			for (int i = 0; i < contours.size(); i++) {
				centers.add(centerofcontour(contours.get(i)));
			}
			//Get Average Centerofmass
			double x = 0;
			double y = 0;
			int centerssize = centers.size();
			for(int i = 0; i < centerssize; i++){
				x = x + centers.get(i).x;
				y = y + centers.get(i).y;
			}
			//long stopTime = System.nanoTime();
			//long elapsedTime = stopTime - startTime;
			//double elapsedTimedobule = elapsedTime/1000000;
		    //System.out.println("Center of Mass for "+cascades.size()+" contours took "+elapsedTimedobule+" ms.");
			return new Point(x/centerssize, y/centerssize);
		} 
		
		public static List<Point> centerofmarkers(List<List<MatOfPoint>> markers) {
			//Get centerofmass for each marker
			List<Point> centerofmarkers = new ArrayList<Point>();
			for(int i = 0; i < markers.size(); i++){
     		    centerofmarkers.add(centerofcontours(markers.get(i)));
			}
			return centerofmarkers;
		} 
		
		public static Mat drawlinesbetweenpoints(Mat image, List<Point> points) {
			if(points.size()>1){
				Scalar colour = new Scalar(0.0,0.0,255.0);
				for(int i = 0; i < points.size()-1; i++){
					Core.line(image, points.get(i), points.get(i+1), colour, 1,  Core.LINE_AA, 0);
				}
				Core.line(image, points.get(points.size()-1), points.get(0), colour, 1,  Core.LINE_AA, 0);
			} else {			
				System.out.println("drawlinesbetweenpoints called with less than 2 Points.");
			}
			return image;
		} 
		
		public static MatOfPoint makepoly(MatOfPoint Points) { 
			MatOfPoint2f Points2f = new MatOfPoint2f();
			MatOfPoint2f Poly2f = new MatOfPoint2f();
			MatOfPoint Poly = new MatOfPoint();
		    Points.convertTo(Points2f, CvType.CV_32FC2);
		    long contourSize = Points2f.total();
		    //System.out.println("The Contour has " + contourSize+" Points.");
		    Imgproc.approxPolyDP(Points2f, Poly2f, 0.04*Imgproc.arcLength(Points2f, true), true);
		    Poly2f.convertTo(Poly, CvType.CV_32S);
			return Poly;
		} 	
		
		public static List<List<MatOfPoint>> dropmarkersbypoly(List<List<MatOfPoint>> markers, int polycount, Mat mat_image, Scalar color) {
			List<List<MatOfPoint>> markersDroppedByPoly = new ArrayList<List<MatOfPoint>>();
		    Iterator<List<MatOfPoint>> iterator = markers.iterator();
		    while(iterator.hasNext()) {
		        List<MatOfPoint> marker = iterator.next();
		        //System.out.println(marker.size()+" elements");
				int dots = 0;
				for(int i = 0; i < marker.size(); i++){
					dots += makepoly(marker.get(i)).total();				
				}
				
				int optimaldotcount = polycount * marker.size();		
				if (dots < optimaldotcount*0.75 || dots > optimaldotcount*1.25){
					iterator.remove();
					markersDroppedByPoly.add(marker);
					//System.out.println(dots+" counted, dropping marker.");
				} else {
					//System.out.println(dots+" counted, keeping marker.");
				}     
		    }
		    for( List<MatOfPoint> marker : markersDroppedByPoly){
		    	Imgproc.drawContours( mat_image, marker, -1, color, 1);
		    }
			//System.out.println("markers has "+markers.size()+" childs.");
			return markers;
		} 
		public static double distance(Point A, Point B) {
			Double dx = A.x - B.x;
			Double dy = A.y - B.y;
			return Math.sqrt(dx*dx + dy*dy);
		} 
		
		public static double pointToLineDistance(Point A, Point B, Point P) {
			double distanceAB = distance(A, B);
			return (P.x-A.x)*(B.y-A.y)-(P.y-A.y)*(B.x-A.x)/distanceAB;
		}
		
		//Get the distance from Point J to a Line between L and M
		public static double cv_lineEquation(Point L, Point M, Point J)
		{
			double a,b,c,dist;
			double slope = lineSlopeFromPoints(L, M);
			a = -slope;
			b = 1.0;
			c = (slope * L.x) - L.y;
			
			// Now that we have a, b, c from the equation ax + by + c, time to substitute (x,y) by values from the Point J

			dist = (a * J.x + (b * J.y) + c) / Math.sqrt((a * a) + (b * b));
			return dist;
		}
		
		public static float lineSlopeFromPoints(Point A, Point B)
		{
			float dx = (float) B.x - (float) A.x;
			float dy = (float) B.y - (float) A.y;

			if(dy == 0){
				return 0;
			} else if (dx == 0) {
				if(dy > 0){
					return 1;
				}else{
					return -1;
				}
			}else{
				return (dy / dx);
			}
		}
				
		public static Point getPointBetweenShortSides(List<Point> Points)
		{
			//get distance between points
			double distanceAB = distance(Points.get(0), Points.get(1));
 			double distanceBC = distance(Points.get(1), Points.get(2));
 			double distanceCA = distance(Points.get(2), Points.get(0));
 			
 			//BC is longest
 			if ( distanceBC > distanceAB && distanceBC > distanceCA ){ 
 				return Points.get(0);
 			//CA is longest
 			}else if ( distanceCA > distanceAB && distanceCA > distanceBC ){
 				return Points.get(1);
 			//AB is longest
 			}else {
 				return Points.get(2);
			}   
		}
		
		public static List<Point> orderPoints(List<Point> Points)
		{
			//Final Order should be {Top, Right, Left}
			List<Point> orderedPoints = new ArrayList<Point>();
			Point top, unknownMarker1, unknownMarker2;
			
			//point between short sides should be the top marker
 		    top = getPointBetweenShortSides(Points);
 		    //put top at the list end
 		    Points.remove(top); 
			
			unknownMarker1 	= Points.get(0);
			unknownMarker2 	= Points.get(1);
		   	
			double dist = cv_lineEquation(unknownMarker1, unknownMarker2, top);	// Get the Perpendicular distance of the outlier from the longest side			
			float slope = lineSlopeFromPoints(unknownMarker1, unknownMarker2);		// Also calculate the slope of the longest side
				
			// Now that we have the orientation of the line formed median1 & median2 and we also have the position of the outlier w.r.t. the line
			// Determine the 'right' and 'bottom' markers

			orderedPoints.add(top);
	
			if (slope <= 0 && dist <= 0 ){			// Orientation - North
				orderedPoints.add(unknownMarker2);
				orderedPoints.add(unknownMarker1);
		   } else if (slope > 0 && dist <= 0 ){		// Orientation - East
			    orderedPoints.add(unknownMarker1);
			    orderedPoints.add(unknownMarker2);
		   } else if (slope <= 0 && dist > 0 ){		// Orientation - South			
			    orderedPoints.add(unknownMarker1);
			    orderedPoints.add(unknownMarker2);
		   } else if (slope > 0 && dist > 0 ){		// Orientation - West
			    orderedPoints.add(unknownMarker2);
			    orderedPoints.add(unknownMarker1);
		   }	
		   return orderedPoints;
		}
		
		
		public static double angle(Point A, Point B, Point C){
			//gets angle between Line 1 (Point A Point B) and Line 2 (Point A Point C)
			Line2D line1 = new Line2D.Double();
		    Line2D line2 = new Line2D.Double();
		    line1.setLine(A.x,A.y,B.x,B.y);  
		    line2.setLine(A.x,A.y,C.x,C.y);  

	        double angle1 = Math.atan2(line1.getY1() - line1.getY2(), line1.getX1() - line1.getX2());
	        double angle2 = Math.atan2(line2.getY1() - line2.getY2(), line2.getX1() - line2.getX2());
	        return angle1-angle2;
		}
		
		public static boolean allPointsInRect(MatOfPoint points, Rect boundingRect){
			List<Point> contourpoints = new ArrayList<Point>();
			boolean insideRect = true;
			contourpoints = points.toList();
			for(int c = 0; c < contourpoints.size(); c++ ){
				if(boundingRect.contains(contourpoints.get(c))){  
					
				} else {  
					insideRect = false;
				}
			}
	        return insideRect;
		}
		
		 private static DefaultMutableTreeNode createTree(){
		        /*
		         * Der Baum wird folgende Form haben:
		         * Wurzel
		         * +- Buchstaben
		         * |  +- A
		         * |  +- B
		         * |  +- C
		         * +- Zahlen
		         *    +- 1
		         *    +- 2
		         *    +- 3
		         */
		 
		        // Zuerst werden alle Knoten hergestellt...
		        DefaultMutableTreeNode root = new DefaultMutableTreeNode( "Wurzel" );
		 
		        DefaultMutableTreeNode letters = new DefaultMutableTreeNode( "Buchstaben" );
		        DefaultMutableTreeNode digits = new DefaultMutableTreeNode( "Zahlen" );
		 
		        DefaultMutableTreeNode letterA = new DefaultMutableTreeNode( "A" );
		        DefaultMutableTreeNode letterB = new DefaultMutableTreeNode( "B" );
		        DefaultMutableTreeNode letterC = new DefaultMutableTreeNode( "C" );
		 
		        DefaultMutableTreeNode digit1 = new DefaultMutableTreeNode( "1" );
		        DefaultMutableTreeNode digit2 = new DefaultMutableTreeNode( "2" );
		        DefaultMutableTreeNode digit3 = new DefaultMutableTreeNode( "3" );
		 
		        // ... dann werden sie verknüpft
		        letters.add( letterA );
		        letters.add( letterB );
		        letters.add( letterC );
		 
		        digits.add( digit1 );
		        digits.add( digit2 );
		        digits.add( digit3 );
		 
		        root.add( letters );
		        root.add( digits );
		 
		        return root;
		    }
		 private static TreePath findPath(DefaultMutableTreeNode root, String s) {
			    @SuppressWarnings("unchecked")
			    Enumeration<DefaultMutableTreeNode> e = root.depthFirstEnumeration();
			    while (e.hasMoreElements()) {
			        DefaultMutableTreeNode node = e.nextElement();
			        if (node.toString().equalsIgnoreCase(s)) {
			            return new TreePath(node.getPath());
			        }
			    }
			    return null;
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
}

