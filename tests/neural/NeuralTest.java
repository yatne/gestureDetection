package neural;

import gesturedetection.neural.Neural;
import gesturedetection.pca.SphericalPcaOutput;
import org.junit.Test;

public class NeuralTest {

    @Test
    public void testNeural(){
        Neural n = new Neural();
        SphericalPcaOutput out1 = new SphericalPcaOutput(new double[]{1.47,0.1044,1.0106,-0.2385,1.369496959272215,0.068587286037613}, 0);
        SphericalPcaOutput out2 = new SphericalPcaOutput(new double[]{1.430953944835096,0.050018453981048,0.969388102056396,-0.303174781361359,1.410691049545514,-0.015235827460775}, 0);
        SphericalPcaOutput out3 = new SphericalPcaOutput(new double[]{1.44615534349198,0.102434939135482,0.833598844401109,-0.278707890780394,1.391206609550766,0.035243161214196}, 0);
        SphericalPcaOutput out4 = new SphericalPcaOutput(new double[]{1.267545217987531,0.150355584305577,0.780663305069649,-0.007497616025407,1.289183759323491,0.117878916879775}, 0);
        SphericalPcaOutput out5 = new SphericalPcaOutput(new double[]{1.482286036144017,0.033198620787394,1.378194769448421,-0.300299285229523,1.471348259334,-0.003702496018407}, 1);
        SphericalPcaOutput out6 = new SphericalPcaOutput(new double[]{1.364318601230346,0.122785013993803,1.373723881144606,-0.260098200456484,1.450112648800239,-0.010238035961154}, 1);
        SphericalPcaOutput out7 = new SphericalPcaOutput(new double[]{1.194041330648302,0.618404149312478,1.502792921786211,-0.295132173172763,1.473337462566554,0.102061946022964}, 1);
        SphericalPcaOutput out8 = new SphericalPcaOutput(new double[]{1.269265345338276,0.650601503438852,1.434575653482126,-0.315948648816128,1.513406158764886,-0.117195180564652}, 1);
        SphericalPcaOutput out9 = new SphericalPcaOutput(new double[]{1.049173393495865,1.056455768234957,0.862168592141051,0.548223833062162,1.248797186306795,1.037922912868197}, 2);
        SphericalPcaOutput out10 = new SphericalPcaOutput(new double[]{0.952736851664748,0.970390349890735,0.959609216907516,0.567420740789661,1.101583358012325,0.862079501926659}, 2);
        SphericalPcaOutput out11 = new SphericalPcaOutput(new double[]{0.97806960445376,1.06556367514567,0.832816304573114,0.578239774846104,1.140419924256715,1.058850489697017}, 2);
        n.addTrainingData(out1);
        n.addTrainingData(out2);
        n.addTrainingData(out3);
        n.addTrainingData(out4);
        n.addTrainingData(out5);
        n.addTrainingData(out6);
        n.addTrainingData(out7);
        n.addTrainingData(out8);
        n.addTrainingData(out9);
        n.addTrainingData(out10);
        n.addTrainingData(out11);
        n.train();
    }
}
//
//1.480609998942784 0.120171850996780 0.857222856675607 -0.126414603021335 1.408159959065251 0.082049582507407 (0,0)
//        1.231826731068000 0.585736355010623 1.439848174172379 -0.382409134831510 1.538742798216103 -0.227534846885209 (0,1)
//        -0.189898556380922 1.225120381816986 0.659840512374679 0.496838888590912 -0.128571700031869 1.312015458851813 (1, 0)
