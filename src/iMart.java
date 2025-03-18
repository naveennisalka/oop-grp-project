import java.util.Scanner;

public class iMart {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        System.out.println("================================================================================");
        System.out.println("__          __  _                            _          _ __  __            _   \n\\ \\        / / | |                          | |        (_)  \\/  |          | |  \n \\ \\  /\\  / /__| | ___ ___  _ __ ___   ___  | |_ ___    _| \\  / | __ _ _ __| |_ \n  \\ \\/  \\/ / _ \\ |/ __/ _ \\| '_ ` _ \\ / _ \\ | __/ _ \\  | | |\\/| |/ _` | '__| __|\n   \\  /\\  /  __/ | (_| (_) | | | | | |  __/ | || (_) | | | |  | | (_| | |  | |_ \n    \\/  \\/ \\___|_|\\___\\___/|_| |_| |_|\\___|  \\__\\___/  |_|_|  |_|\\__,_|_|   \\__|\n");
        System.out.println("================================================================================\n");

        System.out.print("Enter customer Phone Number \t- ");
        String phoneNumber = scanner.nextLine();
        System.out.println();
        System.out.print("Enter customer Name \t- ");
        String name = scanner.nextLine();
        System.out.println();
        System.out.println("================================================================================\n");


        System.out.print("Basmathi Qty(Kg) \t- ");
        int Basmathi = scanner.nextInt();
        System.out.print("Dhal Qty(Kg) \t\t- ");
        int Dhal = scanner.nextInt();
        System.out.print("Sugar Qty(Kg) \t\t- ");
        int Sugar = scanner.nextInt();
        System.out.print("Higland Qty \t\t- ");
        int Higland = scanner.nextInt();
        System.out.print("Yoghurt Qty \t\t- ");
        int Yoghurt = scanner.nextInt();
        System.out.print("Flour Qty(Kg) \t\t- ");
        int Flour = scanner.nextInt();
        System.out.print("Soap Qty \t\t\t- ");
        int Soap = scanner.nextInt();

        System.out.println("\n\n");


        //calculation
        int total  =  250*Basmathi + 180*Dhal + 150*Sugar + 1200*Higland + 50*Yoghurt +120*Flour +160*Soap;



        System.out.println("+---------------------------------------------------------------------------+");
        System.out.print("| \t\t\t\t      _   __  __          _____ _______ ");
        System.out.print("\t\t\t\t    |\n| \t\t\t\t     (_) |  \\/  |   /\\   |  __ \\__   __|");
        System.out.print("\t\t\t\t    |\n| \t\t\t\t      _  | \\  / |  /  \\  | |__) | | |   ");
        System.out.print("\t\t\t\t    |\n| \t\t\t\t     | | | |\\/| | / /\\ \\ |  _  /  | |   ");
        System.out.print("\t\t\t\t    |\n| \t\t\t\t     | | | |  | |/ ____ \\| | \\ \\  | |   ");
        System.out.print("\t\t\t\t    |\n| \t\t\t\t     |_| |_|  |_/_/    \\_\\_|  \\_\\ |_|   ");
        System.out.print("\t\t\t\t    |\n|                        225,Galle Road,Panadura.                           |\n");
        System.out.println("+---------------------------------------------------------------------------+");
        System.out.printf("|                             # %-5s : %-10s                          |\n","Tel",phoneNumber);
        System.out.printf("|                             # %-5s : %-10s                          |\n","Name",name);
        System.out.println("+-----------------------+-------------+------------------+------------------+");
        System.out.println("|                       |     Qty     |    Unit Price    |       Price      |");
        System.out.println("+-----------------------+-------------+------------------+------------------+");
        System.out.printf("| # %-20s|      %-7d|          %-8.1f|          %-8.1f|\n","Basmathi",Basmathi,250.0,Basmathi*250.0);
        System.out.printf("| # %-20s|      %-7d|          %-8.1f|          %-8.1f|\n","Dhal",Dhal,180.0,Dhal*180.0);
        System.out.printf("| # %-20s|      %-7d|          %-8.1f|          %-8.1f|\n","Sugar",Sugar,150.0,Sugar*150.0);
        System.out.printf("| # %-20s|      %-7d|          %-8.1f|          %-8.1f|\n","Higland",Higland,1200.0,Higland*1200.0);
        System.out.printf("| # %-20s|      %-7d|          %-8.1f|          %-8.1f|\n","Yoghurt",Yoghurt,50.0,Yoghurt*50.0);
        System.out.printf("| # %-20s|      %-7d|          %-8.1f|          %-8.1f|\n","Flour",Flour,120.0,Flour*120.0);
        System.out.printf("| # %-20s|      %-7d|          %-8.1f|          %-8.1f|\n","Soap",Soap,160.0,Soap*160.0);
        System.out.println("+-------------------------------------+------------------+------------------+");
        System.out.printf("|                                     |  Total           |          %-8.1f|\n",(float) total);
        System.out.println("|                                     +------------------+------------------+");
        System.out.printf("|                                     |  discount (10%%)  |          %-8.1f|\n",total*0.1);
        System.out.println("|                                     +------------------+------------------+");
        System.out.printf("|                                     |  Price           |          %-8.1f|\n",total*0.9);
        System.out.println("+-------------------------------------+------------------+------------------+");


    }
}
