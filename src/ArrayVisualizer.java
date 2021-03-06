package array.visualizer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import javax.swing.JFrame;

import static array.visualizer.Swaps.*;
import static array.visualizer.BitonicSort.*;
import static array.visualizer.BubbleSort.*;
import static array.visualizer.CocktailShaker.*;
import static array.visualizer.CountingSort.*;
import static array.visualizer.DoubleSelection.*;
import static array.visualizer.GravitySort.*;
import static array.visualizer.InsertionSort.*;
import static array.visualizer.MergeSort.*;
import static array.visualizer.MergeSortOOP.*;
import static array.visualizer.QuickSort.*;
import static array.visualizer.RadixLSD.*;
import static array.visualizer.RadixMSD.*;
import static array.visualizer.Searches.*;
import static array.visualizer.SelectionSort.*;
import static array.visualizer.ShatterSorts.*;
import static array.visualizer.Swaps.*;
import static array.visualizer.TimeSort.*;
import static array.visualizer.WeaveMerge.*;
import static array.visualizer.RadixLSDInPlace.*;
import static array.visualizer.BogoSort.*;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.Instrument;
import javax.swing.JOptionPane;

public class ArrayVisualizer {

    static final int [] array = new int[1000];
    static final JFrame window = new JFrame();
    
    static String heading = "";
    static final ArrayList<Integer> marked = new ArrayList();
    static int frames;
    static int aa = 0;
    static int snd = 0;
    static int comps = 0;
    static long nanos;
    static Font fon = new Font("TimesRoman",Font.PLAIN,(int)(640/1280.0*25));
    static boolean CIRCLEDRAW = true;
    static boolean COLORONLY = false;
    static boolean PIXELDRAW = true;
    static boolean SOUND = false;
    static double SLEEPRATIO = 1.0;
    static UtilFrame uf;
    static ViewPrompt v;
    static Synthesizer synth;
    static MidiChannel chan;
    static Thread sortingThread;
    static boolean SHUFFLEANIM = true;
    
    static String[] ComparativeSorts = "Selection!Bubble!Insertion!Double Selection!Cocktail Shaker!Quick!Merge!Merge OOP!Weave Merge".split("!");
    static String[] DistributiveSorts = "Radix LSD!Radix MSD!Radix LSD In-Place!Gravity!Shatter".split("!");
    
    static int cx = 0;
    static int cy = 0;
    
    public static double calcVel(){
        double count = 1;
        for(int i : marked)
            if(i!=-5)
                count+=0.75;
        return count;
    }
    
    public static synchronized void SetSound(boolean val){
        SOUND = val;
    }
    
    static double addamt = 0.0;
    public static void sleep(double milis){
        if(milis <= 0)
            return;
        double tmp = (milis*(1000.0/array.length));
        tmp = tmp * (1/SLEEPRATIO);
        addamt += tmp - (int)tmp;
        long amt = (long)tmp;
        if(addamt >= 1){
            amt+=(int)addamt;
            addamt -= (int)addamt;
        }
       
        try{
            Thread.sleep(amt);
        }catch(Throwable t){}
    }
    
    public static void main(String[] args) throws Exception {

        synth = MidiSystem.getSynthesizer();
        synth.open();
        synth.loadAllInstruments(synth.getDefaultSoundbank());
//        int s = 0;
//        for(Instrument i : synth.getAvailableInstruments()){
//            System.out.println(s+" "+i.getName());
//            s++;
//        }
        chan = synth.getChannels()[0];
        for(Instrument i : synth.getLoadedInstruments())
            if(i.getName().toLowerCase().trim().contains("sine"))
                chan.programChange(i.getPatch().getProgram());
        
        if(chan.getProgram() == 0)
            JOptionPane.showMessageDialog(null, "Could not find a valid instrument. Sound is disabled");
        //chan.programChange(synth.getLoadedInstruments()[197].getPatch().getProgram());
        
        for(int i = 0; i < array.length; i++)
            marked.add(-5);
        rianr(array);
        window.setSize(new Dimension(640,480));
        window.setLocation(0, 0);
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setTitle("Array Visualizer");
        
        uf = new UtilFrame(window);
        
        //AUDIO THREAD
        new Thread(){
            @Override
            public void run(){
                while(true){
                chan.allNotesOff();
                if(SOUND == false){
                    continue;
                }
                
                int tmp = 1;
                    for(int i : marked)
                        if(i != -5)
                            tmp++;
                    for(int i : marked)
                        if(i != -5){
                            int pitch = (int)Math.round((double)array[Math.min(Math.max(i, 0),array.length-1)]/array.length*96+16);
                            //int vel = (int)(((128-pitch)/320.0+0.4)   *   (128.0/Math.pow(tmp,0.33)));
                            int vel = (int)(64.0/Math.pow(tmp,0.5));
                            chan.noteOn(pitch, vel);
                        }
                            //((int)((127-(array[Math.min(Math.max(i, 0),array.length-1)]/16.0))/Math.sqrt(calcVel())))*5
                            //double tmp = (array[Math.min(Math.max(i, 0),array.length-1)]/32.0+47);
                            //chan.setPitchBend(8192*2-(int)((tmp-Math.floor(tmp))*8192*2));
                    /*
                    ArrayList<Integer> tmp = new ArrayList<Integer>();
                    for(int i : marked)
                        if(i != -5)
                            tmp.add(i);
                    
                    if(tmp.size() > 0){
                        do{
                            int i = tmp.get(snd%tmp.size());
                            snd++;
                        chan.noteOn(array[Math.min(Math.max(i, 0),array.length-1)]/32+47, 127);
                        double tmpd = (array[Math.min(Math.max(i, 0),array.length-1)]/32.0+47);
                        chan.setPitchBend(8192*2-(int)((tmpd-Math.floor(tmpd))*8192*2));
                        }while(false);}*/
                    try{sleep(1);}catch(Exception e){}
                }
            }
        }.start();
        
        //DRAW THREAD
        new Thread(){
            @Override
            public void run(){
                int cw = window.getWidth();
                int ch = window.getHeight();
                Image img = window.createVolatileImage(cw, ch);
                //Graphics g = window.getGraphics();
                Graphics g = img.getGraphics();
                double xscl, yscl;
                while(true){
                    
                    if(window.getWidth()!=cw|| window.getHeight()!=ch || window.getX() != cx || window.getY() != cy){
                        uf.reposition();
                        if(v != null && v.isVisible())
                            v.reposition();
                        cx = window.getX();
                        cy  = window.getY();
                    }
                    
                    if(window.getWidth()!=cw|| window.getHeight()!=ch){
                        cw = window.getWidth();
                        ch = window.getHeight();
                        img = window.createVolatileImage(cw, ch);
                        fon = new Font("TimesRoman",Font.PLAIN,(int)(cw/1280.0*25));
                        g = img.getGraphics();
                        
                    }
                    int gamt = 32;//(int)(frames/1000.0%64);
                    g.setColor(new Color(gamt,gamt,gamt));
                    g.fillRect(0,0,img.getWidth(null),img.getHeight(null));
                    xscl = (double)window.getWidth()/array.length;
                    yscl = (double)(window.getHeight()-30)/array.length;
                    int amt = 0;
                    int circamt = array.length/2;
                    frames++;
                    if(CIRCLEDRAW)
                        for(int i = 0; i < array.length; i++){
                            if(marked.contains(i)||marked.contains(i-1)||marked.contains(i-2)||marked.contains(i-3))
                                g.setColor(Color.BLACK);
                            else
                                g.setColor(getIntColor(array[i]));
                            //COLOR ONLY NO LENGTH
                            if(COLORONLY)
                                g.drawLine(window.getWidth()/2, window.getHeight()/2, window.getWidth()/2+(int)(Math.sin(i*Math.PI/circamt)*(window.getWidth()-64)/2.0), window.getHeight()/2-(int)(Math.cos(i*Math.PI/circamt)*(window.getHeight()-96)/2.0));
                            
                            else if(PIXELDRAW)
                                g.fillRect(window.getWidth()/2+(int)(Math.sin(i*Math.PI/circamt)*((window.getWidth()-64)/2.0*(array[i]/(double)array.length))), window.getHeight()/2-(int)(Math.cos(i*Math.PI/circamt)*((window.getHeight()-96)/2.0*(array[i]/(double)array.length))), (int)(2*(window.getWidth()/640.0)), (int)(2*(window.getHeight()/480.0)));
                            //LENGTH AND COLOR
                            else
                                g.drawLine(window.getWidth()/2, window.getHeight()/2, window.getWidth()/2+(int)(Math.sin(i*Math.PI/circamt)*((window.getWidth()-64)/2.0*(array[i]/(double)array.length))), window.getHeight()/2-(int)(Math.cos(i*Math.PI/circamt)*((window.getHeight()-96)/2.0*(array[i]/(double)array.length))));
                        }
                    else
                        for(int i = 0; i < array.length; i++){
                            if(marked.contains(i)||marked.contains(i-1)||marked.contains(i-2)||marked.contains(i-3))
                                g.setColor(Color.BLACK);
                            else
                                g.setColor(getIntColor(array[i]));
                            
                            int y = 0;
                            int width = (int)(xscl*i)-amt;
                            
                            if(width>0){
                                if(COLORONLY){
                                    y = (int)(window.getHeight()-750*yscl);
                                    g.fillRect(amt, y, width, Math.max((int)(750*yscl),1));
                                    g.setColor(getRevColor());
                                    g.fillRect((int)(i*xscl), y, width, 6);
                                }
                                else if(PIXELDRAW){
                                    y = (int)(window.getHeight()-array[i]*yscl);
                                    g.fillRect(amt, y, width, 3);
                                }
                                else{
                                    y = (int)(window.getHeight()-array[i]*yscl);
                                    g.fillRect(amt, y, width, Math.max((int)(array[i]*yscl),1));
                                    g.setColor(getRevColor());
                                    g.fillRect(amt, y, width, 6);
                                }
                            }
                            amt+=width;
                        }
                    
                    int coltmp = 255;//(int)Math.abs(Math.sin(frames*0.01)*255);
                    g.setColor(new Color(coltmp,coltmp,coltmp));
                    Font f = g.getFont();
                    g.setFont(fon);
                    g.drawString(heading, 10, (int)(cw/1280.0*20)+30);
                    g.drawString("Comparisons: "+comps+" Array Accesses: "+aa, 10, (int)(cw/1280.0*40)+30);
                    g.setFont(f);
                    Graphics g2 = window.getGraphics();
                    g2.setColor(Color.BLACK);
                    g2.drawImage(img, 0, 0, null);
                }
            }
            
            public Color getIntColor(int i) {
                return Color.getHSBColor(((float)i/array.length), 1.0F, 0.8F);
            }
            public Color getRevColor(){
                return getIntColor((int)(Math.sin(frames/66.67)*array.length));
            }
        }.start();
        
        uf.setVisible(false);
        v = new ViewPrompt(window);
        while(v.isVisible()) Thread.sleep(1);
        uf.setVisible(true);
        
        //bogoSort();
//        while(true){
//            clearmarked();
//            
//            //heading = "Linear Search";
//            
//            //Arrays.sort(array);
//            //marked.set(1,730);
//            //linearSearch(730);
//            //refresharray();
//            
//            //heading = "Binary Search";
//            
//            //Arrays.sort(array);
//            //marked.set(1, 730);
//            //binarySearch(730);
//            
//            //heading = "Shatter-Time Sort";
//
//            //timeSort();
//
//            //chan.allNotesOff();
//            //refresharray();
//            weaveMergeSort(0, array.length-1);
//            refresharray();
//            cocktailShakerSort();
//            refresharray();
//            radixLSDsort(4);
//            refresharray()
//  ;RadixLSDInPlace.inPlaceRadixLSDSort(10);refresharray();
//            
//            heading = "Insertion Sort";
//
//            insertionSort();
//
//            chan.allNotesOff();
//            refresharray();
//            heading = "Bubble Sort";
//
//            bubbleSort();
//
//            chan.allNotesOff();
//            refresharray();
//            heading = "Selection Sort";
//
//            selectionSort();
//
//            chan.allNotesOff();
//            refresharray();
//            heading = "Cocktail Shaker Sort";
//
//            cocktailShakerSort();
//
//            chan.allNotesOff();
//            refresharray();
//            heading = "Double Selection Sort";
//
//            doubleSelectionSort(array);
//            
//            chan.allNotesOff();
//            refresharray();
//            heading = "Merge Sort In-Place";
//
//            mergeSort(0, array.length - 1);
//
//            chan.allNotesOff();
//            refresharray();
//            heading = "Merge Sort Out-of-Place";
//            
//            mergeSortOP();
//            
//            chan.allNotesOff();
//            refresharray();
//            
//            heading = "Gravity Sort (Abacus/Bead)";
//            
//            gravitySort();
//            
//            chan.allNotesOff();
//            refresharray();
//
//            heading = "Quick Sort";
//
//            quickSort(array, 0, array.length-1);
//
//            chan.allNotesOff();
//            refresharray();
//            heading = "Counting Sort";
//            
//            countingSort();
//            
//            chan.allNotesOff();
//            refresharray();
//            heading = "Radix LSD Sort";
//
//            radixLSDsort(4);
//
//            chan.allNotesOff();
//            refresharray();
//            heading = "Radix LSD In-Place Sort";
//
//            inPlaceRadixLSDSort(10);
//
//            chan.allNotesOff();
//            refresharray();
//            heading = "Radix MSD Sort";
//            
//            radixMSDSort(4);
//            refresharray();
//            //heading = "Shatter Partition";
//
//            //shatterPartition(1);
//
//            //chan.allNotesOff();
//            //refresharray();
//            heading = "Shatter Sort";
//
//            shatterSort(128);
//
//            chan.allNotesOff();
//            refresharray();
//            //heading = "Simple Shatter Sort";
//
//            //simpleShatterSort(128, 4);
//
//            //chan.allNotesOff();
//            //refresharray();
//        
        //keep on keeping on
        while(window.isActive())Thread.sleep(1);
    }
    
    public static void refresharray() throws Exception {
        clearmarked();
        boolean solved = true;
        for(int i = 0; i < array.length; i++){
            if(array[i]!=i)
                solved = false;
            marked.set(0,i);
        }
        for(int i = 0; i < array.length; i++)
            array[i] = i;
        System.out.println(solved);
        marked.set(0, -5);
        heading = "";
        shuffle(array);
        aa = 0;
        comps = 0;
        clearmarked();
    }
    
    public static int getDigit(int a, int power, int radix){
        return (int) (a / Math.pow(radix, power)) % radix;
    }
    
    public static int[] rianr(int [] arr) {
        for (int i = 0; i < arr.length; i++)
            arr[i] = i;
        shuffle(arr);
        return arr;
    }
    
    public static void clearmarked(){
        for(int i = 0; i < array.length; i++)
            marked.set(i, -5);
    }

    public static void shuffle(int[] array) {
        for(int i = 0; i < array.length; i++){
            swap(array, i, (int)(Math.random()*array.length));
            if(SHUFFLEANIM)
                sleep(1);
        }
    }
    
    /*public static void insertionSort(int slp) {
        int pos;
        for(int i = 1; i < array.length; i++){
            pos = i;
            marked.set(0, i);
            while(pos>0&&array[pos]<=array[pos-1]){
                    swap(array, pos, pos-1,slp);
                    pos--;
            }
        }
    }*/

    public static int sleepTime(double d) {
        return (int)(array.length*d)/4;
    }
    
    public synchronized static void RunAllSorts(){
        if(sortingThread != null)
            while(sortingThread.isAlive()) try {
            Thread.sleep(1);
        } catch (InterruptedException ex) {
            Logger.getLogger(ArrayVisualizer.class.getName()).log(Level.SEVERE, null, ex);
        }

        SetSound(true);
        sortingThread = new Thread(){
            @Override
            public void run(){
        try{
            
            refresharray();
            
            heading = "Insertion Sort";

            insertionSort();

            chan.allNotesOff();
            refresharray();
            heading = "Bubble Sort";

            bubbleSort();

            chan.allNotesOff();
            refresharray();
            heading = "Selection Sort";

            selectionSort();

            chan.allNotesOff();
            refresharray();
            heading = "Cocktail Shaker Sort";

            cocktailShakerSort();

            chan.allNotesOff();
            refresharray();
            heading = "Double Selection Sort";

            doubleSelectionSort(array);
            
            chan.allNotesOff();
            refresharray();
            heading = "Merge Sort In-Place";

            mergeSort(0, array.length - 1);

            chan.allNotesOff();
            refresharray();
            heading = "Merge Sort Out-of-Place";
            
            mergeSortOP();
            
            chan.allNotesOff();
            refresharray();
            
            heading = "Gravity Sort (Abacus/Bead)";
            
            gravitySort();
            
            chan.allNotesOff();
            refresharray();

            heading = "Quick Sort";

            quickSort(array, 0, array.length-1);

            chan.allNotesOff();
            refresharray();
            heading = "Counting Sort";
            
            countingSort();
            
            chan.allNotesOff();
            refresharray();
            heading = "Radix LSD Sort";

            radixLSDsort(4);

            chan.allNotesOff();
            refresharray();
            heading = "Radix LSD In-Place Sort";

            inPlaceRadixLSDSort(10);

            chan.allNotesOff();
            refresharray();
            heading = "Radix MSD Sort";
            
            radixMSDSort(4);
            refresharray();
            //heading = "Shatter Partition";

            //shatterPartition(1);

            //chan.allNotesOff();
            //refresharray();
            heading = "Shatter Sort";

            shatterSort(128);

            chan.allNotesOff();
            refresharray();
        }catch (Exception e){}
        SetSound(false);
            }
        };
        sortingThread.start();
    }
    
    public static void ReportComparativeSort(int n){
        if(sortingThread != null && sortingThread.isAlive())
            return;
        
        final int num = n;
        SetSound(true);
        sortingThread = new Thread(){
            @Override
            public void run(){
                try{
                    
                    refresharray();
                    heading = ComparativeSorts[num]+" Sort";
                switch (num){
                    case 0:
                        selectionSort();break;
                    case 1:
                        bubbleSort();break;
                    case 2:
                        insertionSort();break;
                    case 3:
                        doubleSelectionSort(array);break;
                    case 4:
                        cocktailShakerSort();break;
                    case 5:
                        quickSort(array, 0, array.length-1);break;
                    case 6:
                        mergeSort(0, array.length-1);break;
                    case 7:
                        mergeSortOP();break;
                    case 8:
                        weaveMergeSort(0, array.length-1);break;
                }
                }catch(Exception e){}
                SetSound(false);
            }
        };
        sortingThread.start();
    }
    
    public static void ReportDistributiveSort(int n){
        if(sortingThread != null && sortingThread.isAlive())
            return;
        int bas = 10;
        if(n != 3 && !(n >= 5))
            if(n != 4)
                try{bas = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter Base for Sort"));}catch(Exception e){}
            else
                try{bas = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter Size of Partitions"));}catch(Exception e){}
        
        final int base = Math.max(bas,2);
        final int num = n;
        SetSound(true);
        sortingThread = new Thread(){
            @Override
            public void run(){
        try{
            refresharray();
            heading = DistributiveSorts[num]+" Sort";
        switch (num){
            case 0:
                radixLSDsort(base);break;
            case 1:
                radixMSDSort(base);break;
            case 2:
                RadixLSDInPlace.inPlaceRadixLSDSort(base);break;
            case 3:
                gravitySort();break;
            case 4:
                shatterSort(base);break;
        }
        }catch(Exception e){}
        SetSound(false);
            }
        };
        sortingThread.start();
    }
}
