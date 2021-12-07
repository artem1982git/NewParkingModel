package ParkinModel;

import javax.print.attribute.standard.Sides;
/**
Алгоритмов можно придумать многоЖ
-можно бегать маской размером в зависимости от размера машины по потоку бит и искать подходящее
- можно оптимизировать работу парковщика и занимать маленькой не первое попавшееся место (вдруг рядом свободное и они пригодятся для большой машины), а искать одно свободное
В данном алгоритме одно парковочное место для простоты представляет собой тип long. (Если long делать по 4 бита то алгоритм останется тем же за исключением что мы сможем моделировать больше мест ну и придется заморочится с обработкой бит,
 а именно установкой дополнительных указателей).  В алгоритме идет пробежка по элементам массива при этом попутно создаются указатели на первые вободныебольшие и малеьнике места
Когда они занимаются, от них ведется поиск к концу матрицы для получения новых указателей на первые свободные места.
Когда осовбождаются места под машинами то индексс освободившегося места сравнивается с указателсями на свободные места и если они меньше, то значение указателей меняется на значение индексов освободившегося места.
Также присутствует указатель на последнее занятое место чтобы парковщик обходил не всю парковку
 */
public class Main {
    final static int Size=1000; //ширина и длинна квадратной парковки
    static int ifirstSmallFreePlace=-1; //указатель на первое свободное место чтобы не бегать по массиву и не искать
    static int jfirstSmallFreePlace=-1;
    static int ifirstBigFreePlace=-1; //
    static int jfirstBigFreePlace=-1;

    static int ilastBusyplace=-1; //указатель на последнее занятое место чтобы парквощик вовремя остановился и не шел по пустым местам для их освобождения
    static int jlastBusyPlace=-1;

    final static long parkingArray[][]=new long[Size][Size];// массив где индекс long java сделать не дает
    /*   Cостояния мест:
    в данном примере не будем применять разрядные признаки
0- маленькое место свободно
1- большое место свободно
10- маленькое место занято маленькой машиной
20 - маленькое место занято большой машиной
30-большое место занято большой машиной

 */
    static int smallFree=0;
    static int bigFree=1;
    static int bigBusy=30;
    static int smallBusySmall=10;
    static int smallBusyBig=20;



    public static void main(String[] args) {


        var placeState = Math.random(); //0-small, 1-big
        for (int i = 0; i < Size; i++) { //определяем размер мест в парковке
            for (int j = 0; j < Size; j++) {
                if (placeState <= 0.5) {
                    parkingArray[i][j] = smallFree;
                    if (ifirstSmallFreePlace==-1){ //назначаем указатель на первое пустое маленькое место
                        ifirstSmallFreePlace=i;
                        jfirstSmallFreePlace=j;
                    }
                } else {
                    parkingArray[i][j] = bigFree;//назначаем указатель на первое пустое большое место
                    if(ifirstBigFreePlace==-1){
                        ifirstBigFreePlace=i;
                        jfirstBigFreePlace=j;
                    }
                }

            }
        }

        double smallProb = 0.3; //вероятность появления маленьких машин
        double bigProb = 0.3; //вероятность появления больших машин

        long expCount = 1000; // число экспериментов
        for (long x = 0; x < expCount; x++) {
            int carArrivedEventsCount=1000; //сначало сгенерим столько машин на въезде
            for (long k = 0; k < carArrivedEventsCount; k++) {
                switch (carArrived(smallProb, bigProb)) {
                    case 1:
                        findOneSmallPlace();
                        break;
                    case 2:
                        if (findOneBigPlace()) {
                        } else {
                            findTwoSmallPlaces();
                        }
                        break;
                    default:
                }

            }
            // обходим парковщиком занятые места начиная с первого элемента в массиве и заказнчивая указателями на последнее занятое место
            for (long k = 0; k <= ilastBusyplace * Size + jlastBusyPlace; k++) {

                if (parkingArray[(int) (k / Size)][(int) (k % Size)] == smallBusySmall) {
                    if (releasePlace(0.5)) {
                        releaseSmallCar((int) (k / Size), (int) (k % Size));
                    }
                } else if (parkingArray[(int) (k / Size)][(int) (k % Size)] == smallBusyBig) {
                    if (releasePlace(0.5)) {
                        releaseBigCarFrom2Places((int) (k / Size), (int) (k % Size));
                        k++;
                    } else {
                        k++;
                    }
                } else if (parkingArray[(int) (k / Size)][(int) (k % Size)] == bigBusy) {
                    if (releasePlace(0.5)) {
                        releaseBigCarFromBigPlace((int) (k / Size), (int) (k % Size));
                    }
                }
            }
            //проверим индекс последней занятой машины
            int i=-1;
            int j=-1;
            for (int m=0;m<Size;m++){
                for (int y=0;y<Size;y++){
                    if (parkingArray[m][y]!=smallFree && parkingArray[m][y]!=bigFree){
                        i=m;
                        j=y;
                    }
                }
            }
            if (i!=ilastBusyplace || j!=jlastBusyPlace)
                System.out.println( i +" "+ilastBusyplace +"  "+j+"  "+jlastBusyPlace);

        }
    }





    static boolean findOneBigPlace(){ //найти и занять первое свободное большое место
        if (ifirstBigFreePlace==-1 || jfirstBigFreePlace==-1) // если места заняты
            return false;
        parkingArray[ifirstBigFreePlace][jfirstBigFreePlace]=bigBusy;//занимаем большой машиной
        if (ifirstBigFreePlace>ilastBusyplace || (ifirstBigFreePlace==ilastBusyplace && jfirstBigFreePlace>jlastBusyPlace)){ //смотрим указатель на занятые места
            ilastBusyplace=ifirstBigFreePlace;
            jlastBusyPlace=jfirstBigFreePlace;

        }
        for (int i=ifirstBigFreePlace;i<Size;i++){ // ищем следующее свободное большое место
            for (int j=i==ifirstBigFreePlace?jfirstBigFreePlace:0;j<Size;j++) {
                if(parkingArray[i][j]==bigFree){ //свободно больщое место
                    ifirstBigFreePlace=i;
                    jfirstBigFreePlace=j;
                    return true;
                }
            }
        }
        //если нет нового свободного большого места
        ifirstBigFreePlace=-1;
        jfirstBigFreePlace=-1;

        return true;
    }


    static boolean findOneSmallPlace(){ // ищем и занимаем маленькое свободное место
       if (ifirstSmallFreePlace==-1 || jfirstSmallFreePlace==-1) // если свободных маленьких нет
           return false;

       parkingArray[ifirstSmallFreePlace][jfirstSmallFreePlace]=smallBusySmall;//занимаем малой машиной

        if (ifirstSmallFreePlace>ilastBusyplace || (ifirstSmallFreePlace==ilastBusyplace && jfirstSmallFreePlace>jlastBusyPlace)){ //смотрим указатель на занятые места
            ilastBusyplace=ifirstSmallFreePlace;
            jlastBusyPlace=jfirstSmallFreePlace;

        }
       for (int i=ifirstSmallFreePlace;i<Size;i++){  //  указаьель на следующее маленькое свободное место ( есть ли смысл искать новое следующее свободное место зависит от загрузки парковки. Если машин мало то есть смысл просто пробегатья по массиву по мере необходимости
           for (int j=i==ifirstSmallFreePlace?jfirstSmallFreePlace:0;j<Size;j++) {
               if(parkingArray[i][j]==smallFree) { // находим первое маленькое свободное маленькое
                   ifirstSmallFreePlace = i;
                   jfirstSmallFreePlace = j;

                   return true;
               }
           }
       }
      //если нет нового свободногомаленького места
           ifirstSmallFreePlace=-1;
           jfirstSmallFreePlace=-1;
       return true;
   }

    static boolean findTwoSmallPlaces(){
        if (ifirstSmallFreePlace==-1 || jfirstSmallFreePlace==-1)
            return false;
        for (int i=jfirstSmallFreePlace+1<Size?ifirstSmallFreePlace:ifirstSmallFreePlace+1;i<Size;i++) {  //ищем пару свободных мест
            for (int j = i == ifirstSmallFreePlace ? jfirstSmallFreePlace : 0; j < Size; j++) {
                if (parkingArray[i][j] == smallFree && j < Size - 1 && parkingArray[i][j + 1] == smallFree) {
                    parkingArray[i][j] = smallBusyBig; // занимаем пару маленьких мест
                    parkingArray[i][j + 1] = smallBusyBig;
                    j++;

                    if (i > ilastBusyplace || (i == ilastBusyplace && j > jlastBusyPlace)) { //указатель на занятые места
                        ilastBusyplace = i;
                        jlastBusyPlace = j;
                    }
                    if (i==ifirstSmallFreePlace && j-1==jfirstSmallFreePlace) { //если первое свободное место оказалось со свободным соседом (заняли место)
                        for (int x = (j + 1 < Size) ? i : i + 1; x < Size; x++) { //ищем следующее свободное место
                            for (int y = x == i ? j+1 : 0; y < Size; y++) {
                                if (parkingArray[x][y] == smallFree) {
                                    ifirstSmallFreePlace = x;
                                    jfirstSmallFreePlace = y;
                                    return true;
                                }
                            }
                        }
                        ifirstSmallFreePlace=-1; //нового свободного нет
                        jfirstSmallFreePlace=-1;
                    }

                    return true;

                }
            }
        }
        return false;


    }

    static void checkLastBusyPlace(int i, int j){
        if (i==ilastBusyplace && j== jlastBusyPlace) { //если машина последняя
            int tmpi=i;
            int tmpj=j;
            long count=i*Size+j;
            while (count >=0 && (parkingArray[(int)(count/Size)][(int)(count%Size)]==smallFree || parkingArray[(int)(count/Size)][(int)(count%Size)]==bigFree)){
                count--;

            }
            if (count ==-1){
                ilastBusyplace = -1;
                jlastBusyPlace = -1;
            }else {
                ilastBusyplace=(int)(count/Size);
                jlastBusyPlace=(int)(count%Size);
            }

        }



    }



    static void releaseSmallCar(int i, int j){
        parkingArray[i][j]=smallFree; // свободное маленькое место

        checkLastBusyPlace(i,j);
        if(ifirstSmallFreePlace==-1){ //если нет свободных мест
            ifirstSmallFreePlace=i;
            jfirstSmallFreePlace=j;
            return;
        }

        if ((i<ifirstSmallFreePlace) || (i==ifirstSmallFreePlace && j<jfirstSmallFreePlace)){
           ifirstSmallFreePlace=i;
           jfirstSmallFreePlace=j;
        }

    }

    static void releaseBigCarFromBigPlace(int i, int j){
        parkingArray[i][j]=bigFree; // свободное большое место

        checkLastBusyPlace(i,j);
        if(ifirstBigFreePlace==-1){
            ifirstBigFreePlace=i;
            jfirstBigFreePlace=j;
            return;
        }
        if (i<ifirstBigFreePlace || (i==ifirstBigFreePlace && j<jfirstBigFreePlace)){
            ifirstBigFreePlace=i;
            jfirstBigFreePlace=j;

        }

    }



    static void releaseBigCarFrom2Places(int i, int j){
        parkingArray[i][j]=smallFree; // свободное маленькое место
        parkingArray[i][j+1]=smallFree;
        checkLastBusyPlace(i,j+1);
        if(ifirstSmallFreePlace==-1){
            ifirstSmallFreePlace=i;
            jfirstSmallFreePlace=j;
            return;
        }

        if ((i<ifirstSmallFreePlace) || (i==ifirstSmallFreePlace && j<jfirstSmallFreePlace)){
            ifirstSmallFreePlace=i;
            jfirstSmallFreePlace=j;
        }

    }


    static int carArrived(double smallprob, double bigprob){ //формирует тип прибывшей машины
        if (Math.random()<smallprob) //маленькая машина
            return 1;
        if (Math.random()<smallprob+bigprob) //большая
            return 2;
        return 0; //нет
    }

    static boolean releasePlace(double releaseprob){
        if (Math.random()<releaseprob) //small car arrived
            return true;
        return false;
    }
}
