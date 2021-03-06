/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package array.visualizer;

import static array.visualizer.ArrayVisualizer.*;
import static array.visualizer.Swaps.*;
import static array.visualizer.Analysis.*;
import java.util.ArrayList;

/**
 *
 * @author S630690
 */
public class Transcriptions {
    public static void transcribe(ArrayList<Integer>[] registers, int[] array)throws Exception {
        int total = 0;
        for(int ai = 0; ai < registers.length; ai++){
            for(int i = 0; i < registers[ai].size(); i++){
                sleep(1);
                array[total] = registers[ai].get(i);
                marked.set(1, total);
                total++;
                aa++;
            }
            registers[ai].clear();
        }
    }
    
    public static void transcribenm(ArrayList<Integer>[] registers, int[] array)throws Exception {
        int total = 0;
        for(int ai = 0; ai < registers.length; ai++){
            for(int i = 0; i < registers[ai].size(); i++){
                array[total] = registers[ai].get(i);
                total++;
            }
            registers[ai].clear();
        }
    }
    
    public static void transcribend(ArrayList<Integer>[] registers, int min)throws Exception {
        int total = 0;
        for(int ai = 0; ai < registers.length; ai++){
            for(int i = 0; i < registers[ai].size(); i++){
                sleep((min+i)%5/4);
                array[total+min] = registers[ai].get(i);
                marked.set(1, total+min);
                total++;
                aa++;
            }
            registers[ai].clear();
        }
    }
    
    public static void transcribermsd(ArrayList<Integer>[] registers, int min)throws Exception {
        int total = 0;
        for(ArrayList<Integer> ai : registers)
            total+=ai.size();
        int tmp = 0;
        for(int ai = registers.length-1; ai >= 0; ai--){
            for(int i = registers[ai].size()-1; i >= 0; i--){
                sleep(1+2/registers[ai].size());
                array[total+min-tmp-1] = registers[ai].get(i);
                marked.set(1, total+min-tmp-1);
                tmp++;
                aa++;
            }
        }
    }
    
    public static void transcribe(ArrayList<Integer>[] registers, int [] array, int start) throws Exception {
        int total = start;
        for(int ai = 0; ai < registers.length; ai++){
            for(int i = 0; i < registers[ai].size(); i++){
                sleep(1);
                array[total] = registers[ai].get(i);
                marked.set(1, total);
                total++;
                aa++;
            }
            registers[ai].clear();
        }
    }
    
    public static void inPlaceRadixLSDSort(int radix)throws Exception{
        int pos = 0;
        int[] vregs = new int[radix-1];
        int maxpower = analyze(radix);
        for(int p = 0; p <= maxpower; p++){
            for(int i = 0; i < vregs.length; i++)
                vregs[i]=array.length-1;
            pos = 0;
            for(int i = 0; i < array.length; i++){
                int digit = getDigit(array[pos], p, radix);
                if(digit==0) {
                    pos++;
                    marked.set(0, pos);
                } else {
                    for(int j = 0; j<vregs.length;j++)
                        marked.set(j+1,vregs[j]);
                    swapUpTo(pos,vregs[digit-1]);
                    for(int j = digit-1; j > 0; j--)
                        vregs[j-1]--;
                }
            }
                
        }
    }
    
    public static void fancyTranscribe(ArrayList<Integer>[] registers) throws Exception {
        int[] tmp = new int[array.length];
        boolean[] tmpwrite = new boolean[array.length];
        int radix = registers.length;
        transcribenm(registers, tmp);
        for(int i = 0; i < tmp.length; i++){
            int register = i%radix;
            if(register == 0)
                sleep(radix);//radix
            int pos = (int)(((double)register*((double)tmp.length/radix))+((double)i/radix));
            if(tmpwrite[pos]==false){
                array[pos]=tmp[pos];
                aa++;
                tmpwrite[pos] = true;
            }
            marked.set(register,pos);
        }
        for(int i = 0; i < tmpwrite.length; i++)
            if(tmpwrite[i]==false){
                array[i]=tmp[i];
                aa++;
            }
        clearmarked();
    }
}
