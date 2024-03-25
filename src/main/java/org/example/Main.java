package org.example;
import java.sql.*;
import java.util.Scanner;

public class Main {

    //VARIABLES
    private static int pantallaActual = 0;
    private static java.sql.Connection con;
    private static String usuarioActual;
    private static int idActual;
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_BLUE = "\u001B[34m";


    //MAIN
    public static void main(String[] args) throws Exception {
        String host = "jdbc:sqlite:identifier.sqlite";
        con = java.sql.DriverManager.getConnection(host);
        System.out.println("\n" +
                "  ______   _______    ______    ______   _______   ________ \n" +
                " /      \\ /       \\  /      \\  /      \\ /       \\ /        |\n" +
                "/$$$$$$  |$$$$$$$  |/$$$$$$  |/$$$$$$  |$$$$$$$  |$$$$$$$$/ \n" +
                "$$ |__$$ |$$ |__$$ |$$ |  $$/ $$ |__$$ |$$ |  $$ |$$ |__    \n" +
                "$$    $$ |$$    $$< $$ |      $$    $$ |$$ |  $$ |$$    |   \n" +
                "$$$$$$$$ |$$$$$$$  |$$ |   __ $$$$$$$$ |$$ |  $$ |$$$$$/    \n" +
                "$$ |  $$ |$$ |  $$ |$$ \\__/  |$$ |  $$ |$$ |__$$ |$$ |_____ \n" +
                "$$ |  $$ |$$ |  $$ |$$    $$/ $$ |  $$ |$$    $$/ $$       |\n" +
                "$$/   $$/ $$/   $$/  $$$$$$/  $$/   $$/ $$$$$$$/  $$$$$$$$/ \n");
        while (true) {
            menu();
            int opcion = getOption();
            if (opcion == 0) {
                break;
            }
            if (pantallaActual == 0) {
                switch (opcion) {
                    case 1:
                        top3();
                        break;
                    case 2:
                        login();
                        break;
                    case 3:
                        register();
                        break;
                }
            } else if (pantallaActual == 1) {
                switch (opcion) {
                    case 1:
                        misPartidas();
                        break;
                    case 2:
                        nuevaPartida();
                        break;
                    case 3:
                        reanudarPartida();
                        break;
                    case 4:
                        otrasPartidas();
                        break;
                    case 5:
                        logout();
                        break;

                }
            }
        }
    }

    //MENÚ
    public static void menu() {
        System.out.println(ANSI_CYAN + "-----------------------------------------------------------------------------------------------------------");
        if (pantallaActual == 0) {
            System.out.println("0 Salir | 1 TOP 3 | 2 Login | 3 Register");
        } else {
            System.out.println("0 Salir | 1 Mis Partidas | 2 Nueva Partida | 3 Reanudar Partida | 4 Otras Partidas | 5 Logout " + usuarioActual);
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------" + ANSI_RESET);
    }
    public static int getOption() {
        Scanner scanner = new Scanner(System.in);
        int option = -1;
        try {
            option = Integer.parseInt(scanner.next());
            if ((pantallaActual == 0 && option > 3) || (pantallaActual == 1 && option > 5)) {
                System.out.println("Opción incorrecta");
            }
        } catch (IllegalArgumentException iae) {
            System.out.println("Opción incorrecta");
        }
        return option;
    }

    //OPCIONES
    public static void login() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduce tu Nickname");
        String nombreUsuario = scanner.nextLine();
        PreparedStatement st = null;
        String sql = ("SELECT * FROM usuarios where nickname=?");
        st = con.prepareStatement(sql);
        st.setString(1, nombreUsuario);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            System.out.println("Welcome " + rs.getString(2));
            usuarioActual = rs.getString(2);
            idActual = rs.getInt(1);
            pantallaActual = 1;
        } else {
            System.out.println("No existe este usuario");
            login();
        }
    }
    private static void top3() throws SQLException {
        PreparedStatement st = null;
        String sql = ("SELECT p.id_partida, p.puntuacion, p.tiempo, u.nickname, p.nivel FROM partidas p, usuarios u  WHERE p.id_usuario = u.id ORDER BY p.puntuacion DESC LIMIT 3");
        st = con.prepareStatement(sql);
        ResultSet rs = st.executeQuery();
        System.out.println("TOP 3 PARTIDAS:");
        System.out.println();
        int contador = 1;
        while (rs.next()) {
            if (contador == 1) {
                    System.out.println(ANSI_YELLOW +
                            "\t TOP 1: " + rs.getString(4) + " - " + rs.getInt(2) + "p - " + rs.getInt(3) + "s - " + + rs.getInt(5) + "lvl" + ANSI_RESET);
                    System.out.println();
            } else if (contador == 2) {
                    System.out.println(ANSI_PURPLE +
                            "\t TOP 2: " + rs.getString(4) + " - " + rs.getInt(2) + "p - " + rs.getInt(3) + "s - " + + rs.getInt(5) + "lvl" + ANSI_RESET);
                    System.out.println();
            } else {
                    System.out.println(ANSI_BLUE +
                            "\t TOP 3: " + rs.getString(4) + " - " + rs.getInt(2) + "p - " + rs.getInt(3) + "s - " + + rs.getInt(5) + "lvl" + ANSI_RESET);
                    System.out.println();
            }
            contador++;
        }
    }
    public static void logout(){
        usuarioActual = "nadie";
        idActual = -1;
        pantallaActual = pantallaActual -1;
    }
    public static void register() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        PreparedStatement st = null;
        System.out.println("Nikcname");
        String nickname = scanner.nextLine();
        String sql = "INSERT INTO usuarios (nickname) VALUES(?)";
        st = con.prepareStatement(sql);
        st.setString(1, nickname);
        st.executeUpdate();
    }
    private static void misPartidas() throws  Exception{
        PreparedStatement st = null;
        String sql =("SELECT p.id_partida, p.puntuacion, p.tiempo, p.nivel FROM partidas p, usuarios u  WHERE p.id_usuario = u.id and id_usuario = ? ORDER BY p.puntuacion DESC");
        st = con.prepareStatement(sql);
        st.setInt(1, idActual);
        ResultSet rs = st.executeQuery();
        System.out.println("TUS PARTIDAS:");
        while (rs.next()){
            System.out.println(
                    "ID: " + rs.getInt(1) +
                    "\n\tPuntuacion: " + rs.getInt(2) +
                            " --- Tiempo: " + rs.getInt(3) +
                            " --- Nivel: " + rs.getInt(4));
            System.out.println();
        }
    }
    private static void otrasPartidas() throws  Exception{
        PreparedStatement st = null;
        String sql =("SELECT p.nivel, p.puntuacion, p.tiempo, u.nickname FROM partidas p, usuarios u  WHERE p.id_usuario = u.id and id_usuario != ? ORDER BY p.puntuacion DESC");
        st = con.prepareStatement(sql);
        st.setInt(1, idActual);
        ResultSet rs = st.executeQuery();
        System.out.println("OTRAS PARTIDAS:");
        while (rs.next()){
            System.out.println(
                    "\n\tPuntuacion: " + rs.getInt(2) +
                            " --- Tiempo: " + rs.getInt(3) +
                            " --- Nivel: " + rs.getInt(1) +
                            " --- Nickname: " + rs.getString(4));
            System.out.println();
        }
    }
    public static void nuevaPartida() throws Exception{
        Scanner scanner = new Scanner(System.in);
        int[] resultadosPartida = nuevapartida();
        System.out.println("Puntuacion: " + resultadosPartida[0] + " | Tiempo: " +resultadosPartida[1] + " | Nivel: " + resultadosPartida[2]);
        int puntos = resultadosPartida[0];
        int tiempo = resultadosPartida[1];
        int nivel = resultadosPartida[2];
        scanner.nextLine();
        PreparedStatement st = null;
        String sql = "INSERT INTO partidas (puntuacion, tiempo, nivel, id_usuario) VALUES(?, ?, ?, ?)";
        st = con.prepareStatement(sql);
        st.setInt(1, puntos);
        st.setInt(2, tiempo);
        st.setInt(3, nivel);
        st.setInt(4, idActual);
        st.executeUpdate();
    }
    public static void reanudarPartida() throws Exception{
        System.out.println("Elige la partida que reanudar");
        misPartidas();
        Scanner scanner = new Scanner(System.in);
        int idPartida = scanner.nextInt();
        scanner.nextLine();
        PreparedStatement st0 = null;
        String sql0 = ("SELECT p.nivel from partidas p where p.id_partida = ?");
        st0 = con.prepareStatement(sql0);
        st0.setInt(1, idPartida);
        ResultSet rs = st0.executeQuery();
        int[] pYt = reanudarpartida(rs.getInt(1));
        System.out.println("+ Puntuacion: " + pYt[0] + " | + Tiempo: " +pYt[1] + " | Nivel: " + pYt[2]);
        int puntuacion = pYt[0];
        int tiempo = pYt[1];
        int nivel = pYt[2];
        PreparedStatement st = null;
        String sql = "UPDATE partidas SET puntuacion = puntuacion + ? , tiempo = tiempo + ?, nivel = ?  WHERE id_partida=?";
        st = con.prepareStatement(sql);
        st.setInt(1, puntuacion);
        st.setInt(2, tiempo);
        st.setInt(3, nivel);
        st.setInt(4, idPartida);
        st.executeUpdate();

    }

    //PARTIDAS
    public static int[] nuevapartida() {
        int contador = 0;
        return partida(contador);
    }
    public static int[] reanudarpartida(int nivel) {
        int contador = nivel;
       return partida(contador);
    }
    public static int[] partida(int contador) {
        int[] PyTyN = {0, 0, 0};
        int num1 = 0;
        int num2 = 0;
        int result = 0;
        int operación;
        int respuesta;
        while (true) {
            contador++;
            System.out.println("NIVEL: " + contador);
            operación = (int) (Math.random() * 4);
            if (contador < 10) {
                num1 = (int) (Math.random() * 10);
                num2 = (int) (Math.random() * 10);
            } else if (contador < 20) {
                num1 = (int) (Math.random() * 20);
                num2 = (int) (Math.random() * 20);
            } else if (contador < 30) {
                num1 = (int) (Math.random() * 30);
                num2 = (int) (Math.random() * 30);
            } else if (contador < 40) {
                num1 = (int) (Math.random() * 40);
                num2 = (int) (Math.random() * 40);
            }
            switch (operación) {
                case 0:
                    result = suma(num1, num2);
                    break;
                case 1:
                    result = resta(num1, num2);
                    break;
                case 2:
                    result = mult(num1, num2);
                    break;
                case 3:
                    result = div(num1, num2);
                    break;
            }
            System.out.println("x = ?");
            Scanner scanner = new Scanner(System.in);
            respuesta = scanner.nextInt();
            scanner.nextLine();
            if (result == respuesta) {
                PyTyN[0] = PyTyN[0] + 50;
                System.out.println("+50p!");
            } else{
                System.out.println("Has fallado!");
                break;
            }

        }
        PyTyN[2] = contador;
        int tiempo = contador * 30;
        PyTyN[1] = tiempo;
        return PyTyN;
    }

    //OPERACIONES
    public static int suma(int num1, int num2){
        System.out.println(num1 + " + " + num2 + " = x");
        return num1 + num2;
    }
    public static int resta(int num1, int num2){
        System.out.println(num1 + " - " + num2 + " = x");
        return num1 - num2;
    }
    public static int mult(int num1, int num2){
        System.out.println(num1 + " * " + num2 + " = x");
        return num1 * num2;
    }
    public static int div(int num1, int num2){
        System.out.println(num1 + " / " + num2 + " = x");
        return num1 / num2;
    }
}