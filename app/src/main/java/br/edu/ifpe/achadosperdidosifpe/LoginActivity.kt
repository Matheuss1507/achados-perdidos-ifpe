package br.edu.ifpe.achadosperdidosifpe

import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpe.achadosperdidosifpe.ui.theme.AchadosPerdidosIFPETheme
import br.edu.ifpe.achadosperdidosifpe.ui.theme.IfpeGreen
import br.edu.ifpe.achadosperdidosifpe.ui.theme.IfpeGreenMid
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AchadosPerdidosIFPETheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color(0xFFF8FAFC)
                ) { innerPadding ->
                    LoginScreen(
                        modifier = Modifier.padding(innerPadding),
                        onNavigateToRegister = {
                            val intent = Intent(this, MainActivity::class.java).apply {
                                putExtra("OPEN_SCREEN", "register")
                                flags = FLAG_ACTIVITY_SINGLE_TOP
                            }
                            startActivity(intent)
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onNavigateToRegister: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    fun validarCampos(): Boolean {
        var isValid = true
        if (email.isBlank()) {
            emailError = "O e-mail é obrigatório."
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            emailError = "Informe um e-mail válido."
            isValid = false
        } else {
            emailError = null
        }

        if (password.isBlank()) {
            passwordError = "A senha é obrigatória."
            isValid = false
        } else {
            passwordError = null
        }
        return isValid
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ifpe_logomarca),
                contentDescription = "Logo IFPE Pernambuco",
                modifier = Modifier.height(55.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = painterResource(id = R.drawable.achados_perdidos_logo),
                contentDescription = "Logo Achados e Perdidos",
                modifier = Modifier.size(170.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Conectando pessoas,\ndevolvendo o que é importante.",
                textAlign = TextAlign.Center,
                color = Color(0xFF475569),
                fontSize = 14.sp,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Faça seu login",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        modifier = Modifier.padding(bottom = 14.dp)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (emailError != null) emailError = null
                        },
                        label = { Text("E-mail") },
                        isError = emailError != null,
                        supportingText = {
                            emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Email,
                                contentDescription = "Ícone de e-mail",
                                tint = IfpeGreenMid
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF0F172A),
                            unfocusedTextColor = Color(0xFF0F172A),
                            focusedBorderColor = IfpeGreen,
                            unfocusedBorderColor = Color(0xFFCBD5E1),
                            focusedLabelColor = IfpeGreen,
                            unfocusedLabelColor = Color(0xFF64748B),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            if (passwordError != null) passwordError = null
                        },
                        label = { Text("Senha") },
                        isError = passwordError != null,
                        supportingText = {
                            passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = "Ícone de senha",
                                tint = IfpeGreenMid
                            )
                        },
                        trailingIcon = {
                            val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            val description = if (passwordVisible) "Esconder senha" else "Mostrar senha"
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = description,
                                    tint = Color.Gray
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF0F172A),
                            unfocusedTextColor = Color(0xFF0F172A),
                            focusedBorderColor = IfpeGreen,
                            unfocusedBorderColor = Color(0xFFCBD5E1),
                            focusedLabelColor = IfpeGreen,
                            unfocusedLabelColor = Color(0xFF64748B),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        singleLine = true
                    )
                    Text(
                        text = "Esqueceu sua senha?",
                        color = IfpeGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(vertical = 8.dp)
                            .clickable { /* Ação de recuperação de senha */ }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            if (!validarCampos()) return@Button
                            Firebase.auth.signInWithEmailAndPassword(email.trim(), password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        context.startActivity(
                                            Intent(context, MainActivity::class.java).setFlags(FLAG_ACTIVITY_SINGLE_TOP)
                                        )
                                        (context as? Activity)?.finish()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Falha no login: ${task.exception?.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IfpeGreen)
                    ) {
                        Text(text = "Entrar", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        Row(
            modifier = Modifier.padding(bottom = 16.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Ainda não tem uma conta? ", color = Color(0xFF475569), fontSize = 14.sp)
            Text(
                text = "Cadastre-se",
                color = IfpeGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.clickable {
                    onNavigateToRegister()
                }
            )
        }
    }
}