package Login;


import org.mindrot.jbcrypt.BCrypt;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author kylie
 */
public class AuthUtils {

    // Genera hash de la contraseña
    public static String hashPassword(String passwordPlain) {
        return BCrypt.hashpw(passwordPlain, BCrypt.gensalt(12));
    }

    // Verifica si la contraseña ingresada coincide con el hash
    public static boolean checkPassword(String passwordInput, String storedHash) {
        return BCrypt.checkpw(passwordInput, storedHash);
    }
}
