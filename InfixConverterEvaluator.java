import java.util.*;

public class InfixConverterEvaluator 
{
    public static boolean isOperator(char c) 
    {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    public static int precedence(char op) 
    {
        /*
            Fungsi ini mengembalikan prioritas operator.
            Operator dengan prioritas lebih tinggi akan dieksekusi terlebih dahulu.
            Misalnya, '*' dan '/' memiliki prioritas lebih tinggi daripada '+' dan '-'.
            Prioritas yang lebih tinggi berarti nilai yang lebih besar.

            Contoh : 2 + 3 * 4
            Dalam contoh ini, '*' memiliki prioritas lebih tinggi karena return nya 2 daripada '+' yang returnya 1, sehingga
            3 * 4 dievaluasi terlebih dahulu, menghasilkan 12, dan kemudian 2 + 12 dievaluasi
            menjadi 14.
         */
        switch (op) 
        {
            case '+':
            case '-': return 1;
            case '*':
            case '/': return 2;
        }
        return -1;
    }

    public static boolean isValidInfix(String expr) 
    {
        /*
            regex notation untuk menghapus spasi tanpa mengubah string asli. 
            Barangkali ada user yang tanpa sadar memasukkan spasi
         */
        expr = expr.replaceAll("\\s+", ""); 
        
        /*
            Validasi notasi infix:
            - Tidak boleh kosong
            - Tidak boleh dimulai atau diakhiri dengan operator
            - Tidak boleh ada dua operator berurutan
            - Harus ada setidaknya satu angka
            - (expr.charAt(expr.length() - 1) artinya karakter terakhir dari string expr
            -  isOperator(expr.charAt(0)) artinya karakter pertama dari string expr
         */

        if (expr == null || expr.isEmpty()) 
            return false;
        
        if (expr.isEmpty() || isOperator(expr.charAt(0)) || isOperator(expr.charAt(expr.length() - 1)))
            return false;

        for (char c : expr.toCharArray()) 
        {
            if (!Character.isDigit(c) && !isOperator(c) && c != '(' && c != ')') //checking karakter pertambua bukan angka ( misal simbol atau huruf ) 
            {
                return false;
            }
        }

        for (int i = 1; i < expr.length(); i++) 
        {
            if (isOperator(expr.charAt(i)) && isOperator(expr.charAt(i - 1)))
                return false;
        }
        return true;
    }

    public static String infixToPostfix(String exprInput) 
    {
        Stack<Character> stack = new Stack<>();
        StringBuilder result = new StringBuilder(); // use string builder for efficiency instead of array. Karena StringBuilder lebih efisien daripada String dalam hal penggabungan string
        exprInput = exprInput.replaceAll("\\s+", ""); // menghapus spasi

        for (char c : exprInput.toCharArray()) // iterasi setiap karakter dalam ekspresi input
        {
            if (Character.isDigit(c)) 
            {
                result.append(c); // jika karakter adalah digit, tambahkan ke string hasil
            } 
            else if (isOperator(c)) 
            {
                /*
                 * Penggunaan while disini untuk memastikan bahwa operator dengan prioritas lebih tinggi
                 * di stack dieksekusi terlebih dahulu.
                 * 
                - fungsi !stack.isEmpty() : memeriksa apakah stack tidak kosong
                - fungsi precedence(c) <= precedence(stack.peek()) : memeriksa apakah prioritas operator 
                    saat ini (c) lebih rendah atau sama dengan prioritas operator di atas stack.
                - Jika kedua kondisi tersebut terpenuhi, maka operator di atas stack akan dikeluarkan
                - dan ditambahkan ke string hasil.  
                - Jika kondisi tidak terpenuhi, operator saat ini (c) akan ditambahkan ke stack.
                - Proses ini akan terus berlanjut sampai semua karakter dalam ekspresi input telah diproses.
                 */
                while (!stack.isEmpty() && precedence(c) <= precedence(stack.peek())) 
                {
                    result.append(stack.pop());
                }
                stack.push(c);
            }
        }
        while (!stack.isEmpty()) 
        {
            /*
            - penggunaan while untuk memastikan bahwa semua operator yang tersisa di stack
            - akan ditambahkan ke string hasil setelah semua karakter dalam ekspresi input telah diproses
             */
            result.append(stack.pop());
        }
        return result.toString();
    }

    public static String infixToPrefix(String expr) 
    {
        StringBuilder reversedExprInput = new StringBuilder(expr).reverse(); // Membalikkan string input
        /*
            Mengganti tanda kurung buka dengan tanda kurung tutup dan sebaliknya.
            Ini dilakukan karena dalam notasi prefix, urutan evaluasi berbeda.
            Misalnya, (a + b) * c menjadi c * (b + a) ketika dibalik.
         */
        for (int i = 0; i < reversedExprInput.length(); i++) 
        {
            char c = reversedExprInput.charAt(i);
            if (c == '(') reversedExprInput.setCharAt(i, ')');
            else if (c == ')') reversedExprInput.setCharAt(i, '(');
        }

        /*
        - Menggunakan fungsi infixToPostfix untuk mendapatkan postfix dari ekspresi yang sudah dibalik
        */
        String postfix = infixToPostfix(reversedExprInput.toString()); 
        return new StringBuilder(postfix).reverse().toString(); // mengembalikan hasil postfix yang sudah dibalik
    }

    public static int evaluatePostfix(String expr) 
    {
        Stack<Integer> stack = new Stack<>();
        /*
            Fungsi ini mengevaluasi ekspresi postfix.
            - Menggunakan stack untuk menyimpan operand.
            - Ketika menemukan digit, push ke stack.
            - Ketika menemukan operator, pop dua operand dari stack, lakukan operasi, dan push hasilnya kembali ke stack.
         */
        for (char c : expr.toCharArray()) 
        {
            if (Character.isDigit(c)) 
            {
                stack.push(c - '0');
            } 
            else if (isOperator(c)) 
            {
                int b = stack.pop();
                int a = stack.pop();
                switch (c) {
                    case '+': stack.push(a + b); break;
                    case '-': stack.push(a - b); break;
                    case '*': stack.push(a * b); break;
                    case '/': stack.push(a / b); break;
                }
            }
        }
        return stack.pop();
    }

    public static int evaluatePrefix(String expr) 
    {
        Stack<Integer> stack = new Stack<>();
        for (int i = expr.length() - 1; i >= 0; i--) 
        {
            char c = expr.charAt(i);
            if (Character.isDigit(c)) 
            {
                stack.push(c - '0'); //  c - '0' mengubah karakter digit menjadi nilai integernya
            } 
            else if (isOperator(c)) 
            {
                int a = stack.pop(); // Mengambil operand pertama dari stack
                int b = stack.pop(); // Mengambil operand kedua dari stack
                switch (c) 
                {
                    case '+': stack.push(a + b); break;
                    case '-': stack.push(a - b); break;
                    case '*': stack.push(a * b); break;
                    case '/': stack.push(a / b); break;
                }
            }
        }
        return stack.pop(); // Mengembalikan hasil evaluasi prefix
    }

    public static void main(String[] args) 
    {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n======= Konversi Notasi Infix KELOMPOK 1 BINUS ========");
            System.out.println("1. Masukkan notasi infix dan konversi");
            System.out.println("0. Keluar");
            System.out.print("Pilih menu (0 atau 1): ");
            choice = scanner.nextInt();
            scanner.nextLine(); // konsumsi newline

            switch (choice) 
            {
                case 1 -> {
                    String infixInput;
                    // Perulangan sampai user input valid
                    while (true) {
                        System.out.println("~ Silakan masukkan notasi infix (contoh: 5+4/5, 3*(2+1)): ");
                        infixInput = scanner.nextLine();

                        if (isValidInfix(infixInput)) 
                        {
                            break;
                        } 
                        else 
                        {
                            System.out.println("Notasi infix tidak valid. Silakan coba lagi.\n");
                        }
                    }

                    String postfix = infixToPostfix(infixInput);
                    String prefix = infixToPrefix(infixInput);
                    int hasilPostfix = evaluatePostfix(postfix);
                    int hasilPrefix = evaluatePrefix(prefix);

                    System.out.println("\n✅ Konversi dan Evaluasi Berhasil:");
                    System.out.println("Infix   : " + infixInput);
                    System.out.println("Postfix : " + postfix);
                    System.out.println("Prefix  : " + prefix);
                    System.out.println("Hasil evaluasi Postfix: " + hasilPostfix);
                    System.out.println("Hasil evaluasi Prefix : " + hasilPrefix);
                }
                case 0 -> System.out.println("Program selesai. Terima kasih!");
                default -> System.out.println("Menu tidak tersedia. Silakan pilih ulang.");
            }
        } while (choice != 0);

        scanner.close();
    }

}
