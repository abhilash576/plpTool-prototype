package edu.asu.plp.tool.prototype.view;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import java.util.TreeMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javax.swing.JOptionPane;

public class MemoryVisualizationWindow  extends BorderPane{
	
TreeMap<Long, Object> values = new TreeMap<Long, Object>();

	    boolean wordAligned=false;
	    private long oldSpVal=-1;
	    protected long startAddr = -1;
	    protected long endAddr = -1;
	    Canvas canvas;
	    GraphicsContext gc; 
	    	public MemoryVisualizationWindow(){

	    		HBox topBar = createTopBar();

	    		HBox centerBar = createCenterBar();

	    	    canvas = new Canvas(500, 500);
	    	    
	    	    gc = canvas.getGraphicsContext2D();
	    	    
	            drawShapes(gc);
	            
	            this.setTop(topBar);

	    	    this.setCenter(centerBar);
	    	    
	    	    this.setBottom(canvas);
	    		}

	     
	    	private HBox createTopBar() {

	    		HBox hbox = new HBox();

	    		hbox.setPadding(new Insets(10,10,10,10));

	    	    hbox.setSpacing(90);
	              
	    	    
	    	    Label baseAddrLabel = new Label();
	    	    
	    	    baseAddrLabel.setText("Base Address:");
	    	    
	    	    baseAddrLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
	    		
	    	    Label signedOffsetLabel = new Label();

	    	    signedOffsetLabel.setText("Signed offset (bytes):");

	    	    signedOffsetLabel.setFont(Font.font("Arial", FontWeight.NORMAL,14));
	    	    
	    	    signedOffsetLabel.setLineSpacing(20);
	    	    
	    	    hbox.getChildren().addAll(baseAddrLabel,signedOffsetLabel);
	    	    
	    		    return hbox;

	    		  
	    		}
	    	
	        private HBox createCenterBar() {
	    	

	    		HBox hbox = new HBox();

	    	    hbox.setPadding(new Insets(10,10,10,10));

	    		hbox.setSpacing(10);
	             
	    		
	    		TextField addressText = new TextField();
	    		
	            TextField signedOffsetText = new TextField();
	      
	    	     Button visualizeButton = new Button();
	    	     
	    	     visualizeButton.setText("visualize");
	    	     
	    	     visualizeButton.setOnAction(new EventHandler<ActionEvent>() {
	                 @Override
	                 public void handle(ActionEvent event) {
	                     System.out.println("Visualize");
	                      try {
	                         long addr = Integer.parseInt(addressText.getText());
	                         long base = (addr == -1) ? parseNum(addressText.getText()) : addr;
	                         long offset =parseNum(signedOffsetText.getText());

	                         if(offset < 0) {
	                             startAddr = base + offset;
	                             endAddr = base;
	                         } else {
	                             startAddr = base;
	                             endAddr = base + offset;
	                         }
	                         drawShapes(gc);
	                      
	                     } catch(Exception e) {

	                     }
	                 }
	             });
	      
	    	     hbox.getChildren().addAll(addressText,signedOffsetText,visualizeButton);
	    	     
	    			    return hbox;
	    		
	    		}

	        private void drawShapes(GraphicsContext gc) {
	        	
	            gc.setFont(new Font("Times New Roman",15));
	            
	            FontMetrics fm = Toolkit.getToolkit().getFontLoader().getFontMetrics(gc.getFont());
	            
	            int addrStrOffset =(int) fm.computeStringWidth("0x00000000");
	            
	            int W = (int)canvas.getWidth();
	            
	            int H = (int)canvas.getHeight();
	            
	            int fontHeight = (int)fm.getFont().getSize();
	            
	            long locs = (endAddr - startAddr) / 4 + 1;
	            
	            int topOffset =(int) fm.getFont().getSize() + 10;
	            
	            int rightOffset = 0;

	            gc.setFill(Color.WHITE);
	            
	            gc.fillRect(0, 0, W, H);
	            
	            gc.setFill(Color.rgb(240, 240, 240));
	            
	            gc.fillRect(0, 1, W, topOffset - 1);
	            
	            gc.setFill(Color.BLACK);
	            
	            gc.fillText("Contents", W - 10 - addrStrOffset - rightOffset, 4 + (int) fm.getFont().getSize());
	            
	            gc.fillText("Address", W - 30 - 2*addrStrOffset - rightOffset, 4 + (int) fm.getFont().getSize());
	            
	            gc.fillText("$sp", W - 40 - 2*addrStrOffset - 30 - rightOffset - fm.computeStringWidth("$sp"), 4 + (int) fm.getFont().getSize());

	            
	            if(locs < 1 || startAddr < 0 || endAddr < 0)
	                return;

	            // if the user wants to see more than 32 memory locations, we do
	            // a special case
	            
	            int yScaleFactor = 5;
	            
	            while(locs > Math.pow(2, yScaleFactor) && yScaleFactor < 32)
	                yScaleFactor++;

	            // too big, user wants to visualize more than 32-bit address space
	            if(yScaleFactor == 32)
	                return;
	            
	            long addrOffset = 4;
	            if(yScaleFactor > 5) {
	                locs /= (long) Math.pow(2, yScaleFactor - 5);
	                addrOffset *= Math.pow(2, yScaleFactor - 5);
	            }

	            int rowH = (H - topOffset) / (int) locs;
	            int stringYOffset = (rowH - fontHeight) / 2 + fontHeight;
	            boolean drawStr = (rowH > stringYOffset);

	            for(int i = 0; i < locs; i++) {
	                boolean isMapped = true;

	               Long spVal = (long)29;
	               //System.out.println("read once");
	                if(spVal == null) spVal = oldSpVal;
	                if(spVal >= 0 && spVal >= startAddr + addrOffset*i && spVal < startAddr + addrOffset*i + addrOffset) {
	                    gc.setFill(Color.RED);
	                    double xPoints[] = {W - 40 - 2*addrStrOffset - 30 - rightOffset, W - 40 - 2*addrStrOffset - 30 - rightOffset, W - 40 - 2*addrStrOffset - 10 - rightOffset};
	                    double yPoints[] = {topOffset + i*rowH + rowH / 2 - 5, topOffset + i*rowH + rowH / 2 + 5, topOffset + i*rowH + rowH / 2};
	                    gc.fillPolygon(xPoints,yPoints, 3);
	                    oldSpVal = spVal;
	                }
	              

	                gc.fillRect(W - 20 - addrStrOffset - rightOffset, topOffset + i * rowH, 20 + addrStrOffset, rowH);
	                gc.setFill(Color.rgb(220, 220, 220));
	                gc.strokeLine(0, topOffset + (i+1) * rowH, W, topOffset + (i+1) * rowH);

	                    if(yScaleFactor <= 5 && isMapped) {
	                        gc.setFill(Color.BLACK);
	                        gc.fillText(String.format("0x%08x", (long)(startAddr + addrOffset*i)), W - 10 - addrStrOffset - rightOffset, topOffset + i*rowH + stringYOffset);
	                        System.out.println("start address:"+startAddr);
	                    }
	                    gc.setFill(Color.RED);
	                    gc.fillText(String.format("0x%08x", startAddr + addrOffset*i), W - 10 - 2*addrStrOffset - 20 - rightOffset, topOffset + i*rowH + stringYOffset);
	                    System.out.println("start address:"+startAddr);
	             
	            }

	            gc.setFill(Color.RED);
	            if(yScaleFactor >= 23)
	                gc.fillText((int) (Math.pow(2, (yScaleFactor - 23))) + " Mbytes / div", 5, topOffset + fm.getDescent() + 10);
	            else if(yScaleFactor >= 13)
	                gc.fillText((int) (Math.pow(2, (yScaleFactor - 13))) + " Kbytes / div", 5, topOffset + fm.getDescent() + 10);
	            else
	                gc.fillText((int) (Math.pow(2, (yScaleFactor - 5))) * 4 + " bytes / div", 5, topOffset + fm.getDescent() + 10);

	            gc.fillText(locs + " entries", 5, topOffset + 2*fm.getDescent() + 20);
	            
	         System.out.println("written all");
	     
	    }


	     public Long read(long addr) {
	        return (Long) readReg(addr);
	    }
	     
	      public synchronized final Object readReg(long addr) {
	        if(addr > endAddr || addr < startAddr) {
	            JOptionPane.showMessageDialog(null,"read(" + String.format("0x%08x", addr) + "): Address is out of range.");
	            return null;
	        }
	        else if (wordAligned && addr % 4 != 0) {
	            JOptionPane.showMessageDialog(null,"read(" + String.format("0x%08x", addr) + "): Requested address is unaligned.");
	            return null;
	        }
	        else if(!values.containsKey(addr)) {
	            if(true) {
	                return 0L;
	            }
	           JOptionPane.showMessageDialog(null,"read(" + String.format("0x%08x", addr) + "): Address is not initialized.");
	            return null;
	        }
	        else
	            return values.get(addr);
	    }
	      
	     public static long parseNum(String number) {
	        try {

	        if(number.startsWith("0x") || number.startsWith("0h")) {
	            number = number.substring(2);
	            return Long.parseLong(number, 16);
	        }
	        else if(number.startsWith("0b")) {
	            number = number.substring(2);
	            return Long.parseLong(number, 2);
	        }
	        else
	            return Long.parseLong(number);

	        } catch(Exception e) {
	            
	            JOptionPane.showMessageDialog(null,"Number error: '" + -1 + "' is not a valid number");
	            return -1;
	        }
	    }
	     }

	    	
