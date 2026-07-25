package br.edu.ifpe.achadosperdidosifpe.ui

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpe.achadosperdidosifpe.db.fb.DatabaseProvider
import br.edu.ifpe.achadosperdidosifpe.db.fb.toFBUser
import br.edu.ifpe.achadosperdidosifpe.model.User
import br.edu.ifpe.achadosperdidosifpe.ui.theme.IfpeGreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPage(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {}
) {
    var nomeCompleto by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var matricula by remember { mutableStateOf("") }
    var cursoExpanded by remember { mutableStateOf(false) }
    var cursoSelecionado by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }
    var senhaVisivel by remember { mutableStateOf(false) }
    var confirmarSenhaVisivel by remember { mutableStateOf(false) }

    var nomeError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var cursoError by remember { mutableStateOf<String?>(null) }
    var senhaError by remember { mutableStateOf<String?>(null) }
    var confirmarSenhaError by remember { mutableStateOf<String?>(null) }

    val cursos = listOf(
        "Análise e Desenvolvimento de Sistemas",
        "Engenharia de Computação",
        "Redes de Computadores",
        "Administração",
        "Contabilidade",
        "Outro"
    )
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    fun validarFormulario(): Boolean {
        var isValid = true

        if (nomeCompleto.isBlank()) {
            nomeError = "Digite seu nome completo"
            isValid = false
        } else {
            nomeError = null
        }

        if (email.isBlank()) {
            emailError = "E-mail é obrigatório"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            emailError = "Informe um e-mail válido"
            isValid = false
        } else {
            emailError = null
        }

        if (cursoSelecionado.isBlank()) {
            cursoError = "Selecione seu curso/setor"
            isValid = false
        } else {
            cursoError = null
        }

        if (senha.isBlank()) {
            senhaError = "Senha é obrigatória"
            isValid = false
        } else if (senha.length < 6) {
            senhaError = "A senha deve conter no mínimo 6 caracteres"
            isValid = false
        } else {
            senhaError = null
        }

        if (confirmarSenha.isBlank()) {
            confirmarSenhaError = "Confirme a senha"
            isValid = false
        } else if (senha != confirmarSenha) {
            confirmarSenhaError = "As senhas não coincidem"
            isValid = false
        } else {
            confirmarSenhaError = null
        }

        return isValid
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = IfpeGreen
                )
            }
            Text(
                text = "Criar Conta",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        HorizontalDivider(color = Color(0xFFEEEEEE))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            FormField(label = "Nome Completo *") {
                OutlinedTextField(
                    value = nomeCompleto,
                    onValueChange = {
                        nomeCompleto = it
                        if (nomeError != null) nomeError = null
                    },
                    placeholder = { Text("João da Silva", color = Color.LightGray) },
                    isError = nomeError != null,
                    supportingText = { nomeError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors(),
                    singleLine = true
                )
            }

            FormField(label = "E-mail Institucional *") {
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        if (emailError != null) emailError = null
                    },
                    placeholder = { Text("seu.email@estudante.ifpe.edu.br", color = Color.LightGray) },
                    isError = emailError != null,
                    supportingText = { emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
            }

            FormField(label = "Matrícula (opcional)") {
                OutlinedTextField(
                    value = matricula,
                    onValueChange = { matricula = it },
                    placeholder = { Text("202X0000", color = Color.LightGray) },
                    leadingIcon = {
                        Icon(Icons.Default.Badge, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors(),
                    singleLine = true
                )
            }

            FormField(label = "Curso/Setor *") {
                ExposedDropdownMenuBox(
                    expanded = cursoExpanded,
                    onExpandedChange = { cursoExpanded = it }
                ) {
                    OutlinedTextField(
                        value = cursoSelecionado,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Selecione...", color = Color.LightGray) },
                        isError = cursoError != null,
                        supportingText = { cursoError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                        leadingIcon = {
                            Icon(Icons.Default.School, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = cursoExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(10.dp),
                        colors = fieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = cursoExpanded,
                        onDismissRequest = { cursoExpanded = false }
                    ) {
                        cursos.forEach { curso ->
                            DropdownMenuItem(
                                text = { Text(curso) },
                                onClick = {
                                    cursoSelecionado = curso
                                    cursoExpanded = false
                                    cursoError = null
                                }
                            )
                        }
                    }
                }
            }

            FormField(label = "Senha *") {
                OutlinedTextField(
                    value = senha,
                    onValueChange = {
                        senha = it
                        if (senhaError != null) senhaError = null
                    },
                    placeholder = { Text("Mínimo 6 caracteres", color = Color.LightGray) },
                    isError = senhaError != null,
                    supportingText = { senhaError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
                    },
                    trailingIcon = {
                        IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                            Icon(
                                imageVector = if (senhaVisivel) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (senhaVisivel) "Ocultar senha" else "Mostrar senha",
                                tint = Color.LightGray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }

            FormField(label = "Confirmar Senha *") {
                OutlinedTextField(
                    value = confirmarSenha,
                    onValueChange = {
                        confirmarSenha = it
                        if (confirmarSenhaError != null) confirmarSenhaError = null
                    },
                    placeholder = { Text("Digite a senha novamente", color = Color.LightGray) },
                    isError = confirmarSenhaError != null,
                    supportingText = { confirmarSenhaError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
                    },
                    trailingIcon = {
                        IconButton(onClick = { confirmarSenhaVisivel = !confirmarSenhaVisivel }) {
                            Icon(
                                imageVector = if (confirmarSenhaVisivel) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (confirmarSenhaVisivel) "Ocultar senha" else "Mostrar senha",
                                tint = Color.LightGray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    visualTransformation = if (confirmarSenhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    if (!validarFormulario()) return@Button

                    isLoading = true
                    Firebase.auth.createUserWithEmailAndPassword(email.trim(), senha)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                val uid = task.result?.user?.uid ?: return@addOnCompleteListener
                                val novoUsuario = User(
                                    id = uid,
                                    nome = nomeCompleto,
                                    email = email.trim(),
                                    matricula = matricula.ifBlank { null },
                                    curso = cursoSelecionado,
                                    tipo = "aluno"
                                )
                                DatabaseProvider.database.register(novoUsuario.toFBUser())
                                Toast.makeText(context, "Cadastro realizado com sucesso!", Toast.LENGTH_LONG).show()
                                onNavigateBack()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Falha no cadastro: ${task.exception?.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IfpeGreen)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Text(
                        text = "Criar Conta",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}