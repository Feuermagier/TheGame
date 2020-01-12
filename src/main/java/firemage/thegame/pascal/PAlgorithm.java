package firemage.thegame.pascal;

import java.util.*;
public class PAlgorithm {
    public static int[] getWholeList(short[][]a, short b, int maxmov)
    {
        return listm(a,b,maxmov);
    }
    public static int findm(short[][] a, short b){
        int count = 0;
        for(int i =0; i < a.length; i++){
            for(int j =0; j < a[i].length; j++){
                if(a[i][j]==b){
                    if(i!=0-0.5*(b-1)*(a.length-1)){
                        if(a[i-b][j]==0){
                            count++;

                        }
                        if(j!=0){
                            if(a[i-b][j-1]==(-1*b)){
                                count++;

                            }
                        }
                        if(j!=((a[i].length)-1)){
                            if(a[i-b][j+1]==(-1*b)){
                                count++;

                            }
                        }
                    }
                }
            }
        }
        return count;
    }
    public static int[] listm(short[][] a, short b,int m){
        int [] posmov = new int[3*m];
        int count = 0;

        for(int i =0; i < a.length; i++){
            for(int j =0; j < a[i].length; j++){
                if(a[i][j]==b){
                    if(i!=0-0.5*(b-1)*(a.length-1)){
                        if(a[i-b][j]==0){
                            posmov[count] = i+1;
                            posmov[count+1] = j+1;
                            posmov[count+2] = 0;
                            count+=3;


                        }
                        if(j!=0){
                            if(a[i-b][j-1]==(-1*b)){

                                posmov[count] = i+1;
                                posmov[count+1] = j+1;
                                posmov[count+2] = -1;
                                count+=3;
                            }
                        }
                        if(j!=((a[i].length)-1)){
                            if(a[i-b][j+1]==(-1*b)){

                                posmov[count] = i+1;
                                posmov[count+1] = j+1;
                                posmov[count+2] = 1;
                                count+=3;
                            }
                        }
                    }

                }
            }
        }
        return posmov;
    }

    public static void readfield(int[] a){
        for(int i =0; i < a.length; i++){
            if(i%3==0){
                //System.out.println(" ");
            }
            //System.out.print(a[i] + " ");

        }
    }
    public static void readmatrix(short[][] a){
        for(int i =0; i < a.length; i++){
            for(int j =0; j < a[i].length; j++){
                System.out.print(((a[i][j])*(a[i][j])-(a[i][j])+Math.abs(a[i][j])) + " ");
            }
            System.out.println(" ");
        }
    }
    public static int[] linksshift(int[] algor, int[] maxzuege){
        for(int i = algor.length-1; i>=2; i--){
            if(maxzuege[i]!=0){
                if(algor[i]>=maxzuege[i]){
                    algor[i-2]+=1;
                    for(int j = i-1; j< algor.length; j++){
                        algor[j] = 0;
                    }
                }
            }}
        return algor;


    }
    public static void main(String [] args){
        Scanner sc = new Scanner(System.in);

        long startTime = System.nanoTime();

        short reih = 1;
        short[][] field = new short[][] {
                {-1,-1,-1},
                {-1,-1,-1},
                {0, 0, 0},
                { 1, 1, 1},
                { 1, 1, 1}

        };
        short[][] backup = new short[field.length][field[0].length];
        for(int i = 0; i<field.length; i++){
            for(int j = 0; j <field[0].length; j++){
                backup[i][j]= field[i][j];
            }
        }


        short gewonnen = 0;
        int runde = 0;
        int[] gespzuege = new int[50];
        int[] algor = new int[50];
        int[] maxzuege = new int[50];
        long testruns = 1000000000;

        for(int trynum = 1; trynum <=testruns; trynum++){
            //riesen schleife
            //System.out.println("Das Feld: ");
            //readmatrix(field);
            while( (gewonnen ==0)&&(findm(field,reih)!=0))
            {
                //if(reih==1){System.out.println("weis ist am Zug ");
                //
                //}
                //else{System.out.println("schwarz ist am Zug ");}
                //System.out.println("Die moeglichen Zuege: ");
                //readfield(getWholeList(field,reih));
                int moveCount = findm(field,reih);
                int[] currentmoves = getWholeList(field,reih, moveCount);
                //System.out.println(" ");
                //System.out.println("Der wievielte Zug soll gespielt werden?");
                int welchz = 0;
                if(reih==1){welchz = algor[runde]+1;}
                if(reih==-1){welchz = algor[runde]+1;}
                //System.out.println("Der gewaehlte Zug: " + welchz);
                gespzuege[runde] = welchz;
                maxzuege[runde] = moveCount;
                int[] ausgwzug = new int[3];
                ausgwzug[0] = currentmoves[(welchz-1)*3];
                ausgwzug[1] = currentmoves[(welchz-1)*3+1];
                ausgwzug[2] = currentmoves[(welchz-1)*3+2];

                field[ausgwzug[0]-1][ausgwzug[1]-1] = 0;
                field[ausgwzug[0]-1-reih][ausgwzug[1]-1+ausgwzug[2]] = reih;
                //System.out.println("Das neue Feld: ");
                //atrix(field);
                //System.out.println();
                reih *= -1;

                runde++;
                //System.out.println("Das war runde " + runde);
                for(int lauf = 0; lauf < field[0].length; lauf++){
                    if(field[0][lauf]==1)
                    {gewonnen = 1;}
                    if(field[field.length-1][lauf]==-1)
                    {gewonnen = -1;}
                }
            }





            if(gewonnen==1){	//System.out.println("Weis hat gewonnen ");
                algor[runde-2]+=1; algor[runde-1] = 0;
            }
            else{

                if(gewonnen==-1){	//System.out.println("Schwarz hat gewonnen ");
                    algor[runde-2]+=1;algor[runde-1] = 0;}
                else{
                    if(findm(field,reih)==0){
                        if(reih==1){//System.out.println("Schwarz hat gewonnen ");
                            algor[runde-2]+=1;algor[runde-1] = 0;
                        }
                        else{//System.out.println("Weis hat gewonnen ");
                            algor[runde-2]+=1;algor[runde-1] = 0;}
                    }
                }
            }
            //	System.out.println("Der Algorythmus vor dem linksshift: ");
            for(int i = 0; gespzuege[i]>0; i++){
                //System.out.print(algor[i]);
            }//System.out.println(" ");
            linksshift(algor, maxzuege);

            int durchlauf = 0;
            //System.out.println("Die gespielten Zuege: ");
            //while(gespzuege[durchlauf]>0)
            //{System.out.print(gespzuege[durchlauf]);
            //	durchlauf++;}
            //System.out.println(" ");
            //System.out.println("Der Algorythmus: ");
            //for(int i = 0; gespzuege[i]>0; i++){
            //		System.out.print(algor[i]);
            //}
            //System.out.println(" ");
            //System.out.println("Input any number to start the next Round: ");
            //int unnotig = sc.nextInt();
            if(trynum<testruns){
                runde = 0;
                gewonnen = 0;
                reih = 1;
                for(int i = 0; i<=49; i++){
                    gespzuege[i]=0;
                }
                for(int i = 0; i<field.length; i++){
                    /*for(int j = 0; j <field[0].length; j++){
                        field[i][j] = backup[i][j];
                    }
                    */
                    System.arraycopy(backup[i], 0, field[i], 0, backup[i].length);
                }
            }
            if(algor[1]>=maxzuege[1]){
                System.out.println("ENDSIEG FUER WEIS");
                testruns = 0;
                break;
            } //ende der riesen for schleife
            if(algor[0]>=maxzuege[0]){
                System.out.println("ENDSIEG FUER SCHWARZ");
                testruns = 0;
                break;
            }
            //ende der riesen for schleife
        }
        System.out.println(System.nanoTime()-startTime);
    }
}
