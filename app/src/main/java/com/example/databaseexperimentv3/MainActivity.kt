package com.example.databaseexperimentv3


import android.content.ContentValues.TAG
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.databaseexperimentv3.ui.theme.DatabaseExperimentV3Theme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import android.widget.VideoView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DatabaseExperimentV3Theme {
                // Call the main composable function
                NavigationController()
            }
        }
    }
}


@Composable
fun NavigationController() {

    // Create a NavController
    val navController = rememberNavController()

    // Set up navigation
    NavHost(
        navController = navController,
        startDestination = "ProfilePage"
    ) {
        composable("MainPage") {
            MainPage(navController)
        }
        composable("LoginPage") {
            LoginPage(navController)
        }
        composable("TodoList"){
            TodoApp(navController)
        }
        composable("SignupPage"){
            SignupPage(navController)
        }
        composable("ProfilePage"){
            ProfilePage(navController)
        }
        composable("UserDetailsPage/{username}/{password}") { backStackEntry ->
            // Extract the username and password from backStackEntry
            val username = backStackEntry.arguments?.getString("username")
            val password = backStackEntry.arguments?.getString("password")

            // Call UserDetailsPage composable with username and password
            UserDetailsPage(navController, username, password)
        }
        composable("SplashScreen"){
            SplashScreen(navController)
        }
        composable("SettingsPage"){
            SettingsPage(navController)
        }
        composable("MedalsPage"){
            MedalsPage(navController)
        }

    }
}

@Composable
fun SplashScreen(navController: NavController) {

    // Define the resource ID for your MP4 video
    val videoResourceId = R.raw.compsplash

    Box(
        modifier = Modifier

    ) {
        AndroidView(
            factory = { ctx ->
                val videoView = VideoView(ctx)

                val videoUri = Uri.parse("android.resource://${ctx.packageName}/$videoResourceId")
                videoView.setVideoURI(videoUri)

                videoView.setOnPreparedListener { videoView.start() }

                videoView.setOnCompletionListener {
                    // Video has completed, navigate to the destination activity
                    navController.navigate("MainPage")
                }

                videoView
            },
            modifier = Modifier
                .align(Alignment.Center)
                .height(1000.dp)
        )
    }
}

@Composable
fun SettingsPage(navController: NavController){
    // Load the background image using the Painter class
    val backgroundImage = painterResource(id = R.drawable.settings)
    // Allows users to gain xp in activity
    FirebaseAuth.getInstance().currentUser?.uid?.let { AppXPTimer(it) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = backgroundImage,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
        )
    }

    LaunchedEffect(true) {
        delay(3000)
        navController.navigate("ProfilePage")
    }
}
@Composable
fun MedalsPage(navController: NavController) {
    // Load the background image using the Painter class
    val backgroundImage = painterResource(id = R.drawable.medals)
    // Allows users to gain xp in activity
   // FirebaseAuth.getInstance().currentUser?.uid?.let { AppXPTimer(it) }

    // State to track whether the text should be visible
    var isTextVisible by remember { mutableStateOf(false) }

    // State to track the user's level
    var userLevel by remember { mutableStateOf(0) }

    userLevel = FirebaseAuth.getInstance().currentUser?.uid?.let { getUserLevel(it)}!!

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = backgroundImage,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        // Empty Button with alpha set to 0
        Button(
            onClick = { navController.navigate("ProfilePage") },
            modifier = Modifier
                .size(width = 130.dp, height = 80.dp)
                .offset(y = (745).dp, x = (135).dp)
                .alpha(0f)
        ) {}

        // Clickable Image (displayed only if the user is level 5 or greater)
        if (userLevel >= 5) {
            Image(
                painter = painterResource(id = R.drawable.nextlevel),
                contentDescription = null,
                modifier = Modifier
                    .size(width = 55.dp, height = 55.dp)
                    .offset(y = (170).dp, x = (60).dp)
                    .clickable {
                        // When clicked, show the text for 3 seconds
                        isTextVisible = true
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(3000)
                            isTextVisible = false
                        }
                    }
            )
        }

        // Display text if isTextVisible is true
        if (isTextVisible) {
            Text(
                text = "Reach Level 5",
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(Color.White)
            )
        }
    }
}
@Composable
fun MainPage(navController: NavController) {

    // Load the background image using the Painter class
    val backgroundImage = painterResource(id = R.drawable.starting_page)

    // Define the resource ID for your MP4 video
    val videoResourceId = R.raw.pulsing_heart

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = backgroundImage,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
                .scale(1.12f)
        )

        // VideoView for the Pulsing Heart
        AndroidView(
            factory = { context ->
                val videoView = VideoView(context)

                // Set the video URI from the resource
                val videoUri = Uri.parse("android.resource://${context.packageName}/$videoResourceId")
                videoView.setVideoURI(videoUri)

                // Start playing when the view is ready
                videoView.start()

                // Set an OnCompletionListener to restart the video when it completes
                videoView.setOnCompletionListener { mediaPlayer ->
                    mediaPlayer.start()
                }

                videoView
            },
            modifier = Modifier
                .align(Alignment.Center)
                .absoluteOffset(y = (-120).dp)
                .size(200.dp)
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "",
                style = TextStyle(fontSize = 24.sp),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Box(
            modifier = Modifier
                .absoluteOffset(y = (-200).dp, x = (-9).dp)
                .align(Alignment.BottomCenter)
        ) {
            Button(
                onClick = {
                    navController.navigate("LoginPage")
                },
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .width(240.dp)
                    .alpha(0f)
            ) {
                Text("")
            }
        }

        // Add the second button with a different offset
        Box(
            modifier = Modifier
                .absoluteOffset(y = (-140).dp, x = (-9).dp)
                .align(Alignment.BottomCenter)
        ) {
            Button(
                onClick = {
                    navController.navigate("SignupPage")
                },
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .width(240.dp)
                    .alpha(0f)
            ) {
                Text("")
            }
        }
    }
}


@Composable
fun LoginPage(navController: NavController){


    // Initialize Firebase Auth
    val auth: FirebaseAuth = Firebase.auth

    // Load the background image using the Painter class
    val backgroundImage = painterResource(id = R.drawable.loginpage)

    // State for the text input in the TextBox
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // State for displaying success or error messages
    var showMessage by remember { mutableStateOf(false) }
    var messageText by remember { mutableStateOf("") }

    val context = LocalContext.current
    // Load our warping sounds.
    val mediaPlayer = MediaPlayer.create(context, R.raw.warp)

    // Function to handle user login
    fun signIn() {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    // User login successful
                    messageText = "Login Successful"
                    showMessage = true

                    // Navigate user to profile
                    navController.navigate("profilePage")

                    // Clear the input fields after login
                    email = ""
                    password = ""
                }
                .addOnFailureListener { e ->
                    // User login failed
                    messageText = "Login Failed: ${e.message}"
                    showMessage = true
                }
                .addOnCompleteListener {
                    // Release the media player after the login attempt is complete
                    mediaPlayer.release()
                }
        } else {
            // Handle the case when either email or password is empty
            messageText = "Email and password cannot be empty"
            showMessage = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = backgroundImage,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
                .scale(1.12f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = "",
                style = TextStyle(fontSize = 24.sp),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        // Add a text input field (TextBox)
        BasicTextField(
            value = email,
            onValueChange = {
                email = it
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    // You can perform an action when the user presses Done here
                }
            ),
            modifier = Modifier
                .offset(y = (-83f).dp, x = 8.dp)
                .align(Alignment.Center)
                .width(280.dp)
                .height(55.dp)
                .background(Color.Transparent)
                .padding(16.dp),

            textStyle = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal
            )
        )

        BasicTextField(
            value = password,
            onValueChange = {
                password = it
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    // You can perform an action when the user presses Done here
                }
            ),
            modifier = Modifier
                .offset(y = (-3f).dp, x = 8.dp)
                .align(Alignment.Center)
                .width(280.dp)
                .height(55.dp)
                .background(Color.Transparent)
                .padding(16.dp),

            textStyle = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal
            )
        )
        Box(
            modifier = Modifier
                .absoluteOffset(y = (-278).dp, x = (0).dp)
                .align(Alignment.BottomCenter)
        ) {
            Button(
                onClick = {
                    mediaPlayer.start()
                    signIn()
                },
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .width(270.dp)
                    .height(50.dp)
                    .alpha(0f)
            ) {
                Text("")
            }

        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Text(
            text = if (showMessage) messageText else "",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                shadow = Shadow(
                    color = Color.Red, // Shadow color
                    offset = Offset(4F, 4F), // Shadow offset
                    blurRadius = 8F // Shadow blur radius
                )
            ),

            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.Center)
                .offset(y = ((170).dp))
                .alpha(if (showMessage) 1f else 0f) // Show/hide the message
        )
    }

    // Create a coroutine scope
    val coroutineScope = rememberCoroutineScope()

    // Use LaunchedEffect to automatically reset showMessage after a delay
    LaunchedEffect(showMessage) {
        if (showMessage) {
            coroutineScope.launch {
                delay(3000L) // Delay for 3 seconds (3000 milliseconds)
                showMessage = false // Reset showMessage after the delay
            }
        }
    }


}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignupPage(navController: NavController){

    // Load the background image using the Painter class
    val backgroundImage = painterResource(id = R.drawable.sign_up_page)

    // Access the keyboard controller locally
    val keyboardController = LocalSoftwareKeyboardController.current

    // State for the text input and storing user data
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // State for displaying success or error messages
    var showMessage by remember { mutableStateOf(false) }
    var messageText by remember { mutableStateOf("") }

    // Function to handle user registration and data storage

    fun signUp(navController: NavController) {

        // Regular expression patterns for email and password validation
        val emailPattern = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")
        val passwordPattern = Regex("^(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")

        // Trim the input username to remove leading and trailing spaces
        val trimmedUsername = username.trim()

        // Check if the trimmed username and password are not empty
        if (trimmedUsername.isNotEmpty() && password.isNotEmpty()) {
            // Check if the provided email is a valid email
            if (trimmedUsername.matches(emailPattern)) {
                // Check if the password meets the criteria
                if (password.matches(passwordPattern)) {

                    // Pass the trimmed username and password to UserDetailsPage
                    navController.navigate("UserDetailsPage/$trimmedUsername/$password")

                } else {
                    // Password does not meet the criteria
                    messageText = "Password must be at least 8 characters long with at least one uppercase letter and one symbol."
                    showMessage = true
                }
            } else {
                // Username is not a valid email
                messageText = "Please enter a valid email address."
                showMessage = true
            }
        } else {
            // Handle the case when either username or password is empty
            messageText = "Username and password cannot be empty."
            showMessage = true
        }
    }


    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = backgroundImage,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
                .scale(1.12f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = "",
                style = TextStyle(fontSize = 24.sp),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // ... (previous code)

            Text(
                text = if (showMessage) messageText else "",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    shadow = Shadow(
                        color = Color.Red, // Shadow color
                        offset = Offset(4F, 4F), // Shadow offset
                        blurRadius = 8F // Shadow blur radius
                    )
                ),

                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.Center)
                    .offset(y = ((150).dp))
                    .alpha(if (showMessage) 1f else 0f) // Show/hide the message
            )
        }


        HintTextField(
            value = username,
            onValueChange = {
                username = it
            },
            hint = "Pulse@PulseCreate.com",
            modifier = Modifier
                .offset(y = (-83f).dp, x = (-5).dp)
                .align(Alignment.Center)
                .width(280.dp)
                .height(55.dp)
                .background(Color.Transparent)
                .padding(16.dp),
            textStyle = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal
            )
        )

        HintTextField(
            value = password,
            onValueChange = {
                password = it
            },
            hint = "Password123$",
            modifier = Modifier
                .offset(y = (-3f).dp, x = (-5).dp)
                .align(Alignment.Center)
                .width(280.dp)
                .height(55.dp)
                .background(Color.Transparent)
                .padding(16.dp),
            textStyle = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
            )
        )

        Box(
            modifier = Modifier
                .absoluteOffset(y = (-278).dp, x = (0).dp)
                .align(Alignment.BottomCenter)
        ) {
            Button(
                onClick = {
                    keyboardController?.hide()
                    signUp(navController)
                },
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .width(270.dp)
                    .height(50.dp)
                    .alpha(0f)
            ) {
                Text("")
            }

        }

    }
    // Use LaunchedEffect to automatically reset showMessage after a delay
    LaunchedEffect(showMessage) {
        if (showMessage) {
            coroutineScope.launch {
                delay(3000L)
                showMessage = false // Reset showMessage after the delay
            }
        }
    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun UserDetailsPage(navController: NavController, username: String?, password: String?) {

    // Load the background image using the Painter class
    val backgroundImage = painterResource(id = R.drawable.user_details_page)

    // Access the keyboard controller locally
    val keyboardController = LocalSoftwareKeyboardController.current

    // State for the text input and storing user data
    var playerHandle by remember { mutableStateOf("") }
    var birthdate by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    // State for displaying success or error messages
    var showMessage by remember { mutableStateOf(false) }
    var messageText by remember { mutableStateOf("") }

    // Function to handle user registration and data storage

    fun signUp(navController: NavController) {
        // Check if Player Handle is not empty and doesn't exceed 12 characters
        if (playerHandle.isNotEmpty() && playerHandle.length <= 12) {
            // Check if Birthdate is in a valid format (e.g., mm-dd-yyyy)
            val birthdatePattern = Regex("^\\d{2}-\\d{2}-\\d{4}$")
            if (birthdate.isNotEmpty() && birthdate.matches(birthdatePattern)) {
                // Check if Gender is one of the allowed values (e.g., Male, Female, Other)
                val allowedGenders = setOf("Male", "Female", "Other")
                if (gender.isNotEmpty() && gender in allowedGenders) {
                    // Check if Location is not empty
                    if (location.isNotEmpty()) {
                        // If all inputs are valid, proceed with user registration
                        if (username != null) {
                            if (password != null) {
                                FirebaseAuth.getInstance().createUserWithEmailAndPassword(username, password)
                                    .addOnSuccessListener { authResult ->
                                        // User registration successful
                                        messageText = "Registration Successful"
                                        showMessage = true

                                        // Store additional user data in Firestore
                                        val userId = authResult.user?.uid
                                        val db = FirebaseFirestore.getInstance()
                                        userId?.let {
                                            val user = hashMapOf(
                                                "playerHandle" to playerHandle,
                                                "birthdate" to birthdate,
                                                "gender" to gender,
                                                "location" to location,
                                                "xp" to 0
                                            )
                                            db.collection("users").document(it)
                                                .set(user)
                                                .addOnSuccessListener {
                                                    // Data stored successfully
                                                    // Clear the input fields after registration
                                                    playerHandle = ""
                                                    birthdate = ""
                                                    gender = ""
                                                    location = ""

                                                    // Navigate to the next screen
                                                    navController.navigate("ProfilePage")
                                                }
                                                .addOnFailureListener { e ->
                                                    // Handle Firestore data storage error
                                                    messageText = "Firestore Data Storage Failed: ${e.message}"
                                                    showMessage = true
                                                }
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        // User registration failed
                                        messageText = "Registration Failed: ${e.message}"
                                        showMessage = true
                                    }
                            }
                        }
                    } else {
                        messageText = "Location cannot be empty."
                        showMessage = true
                    }
                } else {
                    messageText = "Invalid gender. Please select from Male, Female, or Other."
                    showMessage = true
                }
            } else {
                messageText = "Invalid birthdate format. Please use mm-dd-yyyy."
                showMessage = true
            }
        } else {
            messageText = "Player Handle should not be empty and must not exceed 12 characters."
            showMessage = true
        }
    }

    // Create a coroutine scope
    val coroutineScope = rememberCoroutineScope()


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = backgroundImage,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
                .scale(1.12f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = "",
                style = TextStyle(fontSize = 24.sp),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // ... (previous code)

            Text(
                text = if (showMessage) messageText else "",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    shadow = Shadow(
                        color = Color.Red, // Shadow color
                        offset = Offset(4F, 4F), // Shadow offset
                        blurRadius = 8F // Shadow blur radius
                    )
                ),

                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.Center)
                    .offset(y = ((150).dp))
                    .alpha(if (showMessage) 1f else 0f) // Show/hide the message
            )
        }


        HintTextField(
            value = playerHandle,
            onValueChange = {
                playerHandle = it
            },
            hint = "PulseMaster",
            modifier = Modifier
                .offset(y = (-83f).dp, x = (-5).dp)
                .align(Alignment.Center)
                .width(280.dp)
                .height(55.dp)
                .background(Color.Transparent)
                .padding(16.dp),
            textStyle = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal
            )
        )

        HintTextField(
            value = birthdate,
            onValueChange = {
                birthdate = it
            },
            hint = "05-12-1996",
            modifier = Modifier
                .offset(y = (-3f).dp, x = (0).dp)
                .align(Alignment.Center)
                .width(280.dp)
                .height(55.dp)
                .background(Color.Transparent)
                .padding(16.dp),
            textStyle = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
            )
        )

        HintTextField(
            value = gender,
            onValueChange = {
                gender = it
            },
            hint = "Female",
            modifier = Modifier
                .offset(y = 83.dp, x = (0).dp)
                .align(Alignment.Center)
                .width(280.dp)
                .height(55.dp)
                .background(Color.Transparent)
                .padding(16.dp),
            textStyle = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
            )
        )

        HintTextField(
            value = location,
            onValueChange = {
                location = it
            },
            hint = "United States",
            modifier = Modifier
                .offset(y = 170.dp, x = (0).dp)
                .align(Alignment.Center)
                .width(280.dp)
                .height(55.dp)
                .background(Color.Transparent)
                .padding(16.dp),
            textStyle = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
            )
        )

        Box(
            modifier = Modifier
                .absoluteOffset(y = (-65).dp, x = (10).dp)
                .align(Alignment.BottomCenter)
        ) {
            Button(
                onClick = {
                    keyboardController?.hide() // Hide the keyboard
                    signUp(navController)
                },
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .width(270.dp)
                    .height(50.dp)
                    .alpha(0f)
            ) {
                Text("")
            }

        }

    }
    // Use LaunchedEffect to automatically reset showMessage after a delay
    LaunchedEffect(showMessage) {
        if (showMessage) {
            coroutineScope.launch {
                delay(3000L) // Delay for 3 seconds (3000 milliseconds)
                showMessage = false // Reset showMessage after the delay
            }
        }
    }

}

@Composable
fun HintTextField(value: String, onValueChange: (String) -> Unit, hint: String, modifier: Modifier = Modifier, textStyle: TextStyle = TextStyle.Default) {
    var isHintDisplayed by remember { mutableStateOf(value.isEmpty()) }

    BasicTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
            isHintDisplayed = it.isEmpty()
        },
        textStyle = textStyle,
        modifier = modifier
    )

    if (isHintDisplayed) {
        Text(
            text = hint,
            style = textStyle.copy(color = Color.Gray),
            modifier = modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun GetUser(onUserLoaded: (User) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    val userId = currentUser?.uid
    val usersRef = db.collection("users")
    val userDocRef = userId?.let { usersRef.document(it) }

    if (currentUser != null) {
        userDocRef?.get()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    // Parse user data from the Firestore document
                    val playerHandle = document.getString("playerHandle")
                    val gender = document.getString("gender")
                    val location = document.getString("location")
                    val userXP = document.getLong("xp")?.toInt() ?: 0
                    // Create a User object with the fetched data
                    val user = User(playerHandle, gender, location, userXP)

                    // Call the callback function with the user data
                    onUserLoaded(user)
                } else {
                    Log.d(TAG, "The document doesn't exist. SETUP FAILED")
                }
            } else {
                task.exception?.message?.let {
                    Log.d(TAG, it)
                }
            }
        }
    } else {
        Log.d(TAG, "No user is currently logged in. SETUP FAILED")
    }
}
@Composable
fun getUserLevel(userId: String): Int {
    // Use remember to persist the value across recompositions
    var userXP by remember { mutableIntStateOf(0) }

    // Use LaunchedEffect to trigger the side effect of fetching user XP
    LaunchedEffect(userId) {
        // Perform the network request or any other side effect here
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(userId)

        // Fetch the user's XP from Firebase
        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val userXPFromFirebase = document.getLong("xp")?.toInt() ?: 0
                // Update the userXP value
                userXP = (userXPFromFirebase / 1000) + 1
            }
        }
    }

    // Return the userXP value
    return userXP
}

data class User(
    val playerHandle: String?,
    val gender: String?,
    val location: String?,
    val xp: Int,
)

fun adminLogin(){
    val email = "Nickanick12@gmail.com"
    val password = "Camera12$"
    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
}

@Composable
fun AppXPTimer(userId: String) {
    // Create a state to hold the user's XP
    var userXP by remember { mutableIntStateOf(0) }

    DisposableEffect(Unit) {
        val xpPerSecond = 5 // XP to give every second

        val job = CoroutineScope(Dispatchers.Default).launch {
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("users").document(userId)

            while (true) {
                // Fetch the user's XP from Firebase
                val document = userRef.get().await()
                if (document.exists()) {
                    val userXPFromFirebase = document.getLong("xp")?.toInt() ?: 0
                    userXP = userXPFromFirebase
                }

                delay(1000)
                userXP += xpPerSecond

                // Update the user's XP in Firebase
                userRef.update("xp", userXP)
            }
        }

        onDispose {
            job.cancel()
        }
    }
}

fun grantXP(userId: String) {
    val xpToAdd = 1000

    val db = FirebaseFirestore.getInstance()
    val userRef = db.collection("users").document(userId)

    userRef.get().addOnSuccessListener { document ->
        if (document.exists()) {
            val userXPFromFirebase = document.getLong("xp")?.toInt() ?: 0
            val updatedXP = userXPFromFirebase + xpToAdd

            // Update the user's XP in Firebase
            userRef.update("xp", updatedXP)
        }
    }
}

@Composable
fun ProfilePage(navController: NavController) {
    // START Debugging ADMIN Login
    adminLogin()
    // END Debugging ADMIN Login

    // Load the background image using the Painter class
    val backgroundImage = painterResource(id = R.drawable.profile)
    val xpImage = painterResource(id = R.drawable.level)

    // State to hold user data
    var user: User by remember { mutableStateOf(User(null, null, null, 0)) }

    // Call the GetUser function and update the user object when the data is available
    GetUser { currentUser ->
        user = currentUser
    }

    // Access user data through the user object
    val playerHandle = user.playerHandle ?: "Loading"
    val gender = user.gender ?: "Loading..."
    val location = user.location ?: "Loading..."
    val userXP = user.xp

    // Allows user to gain experience passively
    FirebaseAuth.getInstance().currentUser?.uid?.let { AppXPTimer(it) }

    // Main Background & Imagery
    Box(
        modifier = Modifier
            .background(Color(0xFF6200EA))
    ) {
        Image(
            painter = xpImage,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .absoluteOffset(320.dp, 90.dp)
                .zIndex(10f)
                .size(50.dp, 50.dp)
        )

        Image(
            painter = backgroundImage,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
        )

        // Player Handle
        Box(
            modifier = Modifier
                .padding(16.dp)
                .absoluteOffset(100.dp, 130.dp)
        ) {
            Text(
                text = playerHandle,
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Cyan,
                modifier = Modifier.padding(8.dp)
            )
        }

        // Profile Picture
        Surface(
            modifier = Modifier
                .size(87.dp)
                .padding(5.dp)
                .offset(35.dp, 120.dp),
            shape = CircleShape,
            border = BorderStroke(0.5.dp, Color.LightGray),
            color = Color.Transparent
        ) {
            Image(
                painter = painterResource(id = R.drawable.man),
                contentDescription = "profile image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(134.dp)
            )
        }

        // XP BAR
        Card(
            modifier = Modifier
                .height(30.dp)
                .width(300.dp)
                .padding(8.dp)
                .absoluteOffset(30.dp, 100.dp)
                .border(2.5.dp, Color.DarkGray, CircleShape)
        ) {
            LinearProgressIndicator(
                progress = (userXP % 1000).toFloat() / 1000,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Current Level
        Box(
            modifier = Modifier
                .padding(16.dp)
                .absoluteOffset(130.dp, 95.dp)
        ) {
            Text(
                text = "Current Level: ${userXP / 1000}",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Green,
                modifier = Modifier.padding(8.dp)
            )
        }

        // Gender
        Box(
            modifier = Modifier
                .padding(16.dp)
                .absoluteOffset(137.dp, 183.dp)
        ) {
            Text(
                text = gender,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray,
                modifier = Modifier.padding(8.dp)
            )
        }

        // Location
        Box(
            modifier = Modifier
                .padding(16.dp)
                .absoluteOffset(193.dp, 183.dp)
        ) {
            Text(
                text = location,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray,
                modifier = Modifier.padding(8.dp)
            )
        }

        // Navigation Buttons
        Box(
            modifier = Modifier
                .absoluteOffset(y = (-55).dp, x = (0).dp)
                .align(Alignment.BottomCenter)
        ) {
            Button(
                onClick = {
                    navController.navigate("TodoList")
                },
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .width(270.dp)
                    .height(50.dp)
                    .alpha(0f)
            ) {
                Text("")
            }

            Button(
                onClick = {
                    navController.navigate("MainPage")
                    Firebase.auth.signOut()
                },
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .offset(x = 250.dp, y = (-710).dp)
                    .width(50.dp)
                    .height(50.dp)
                    .alpha(0f)
            ) {
                Text("")
            }

            Button(
                onClick = {
                    navController.navigate("SettingsPage")
                },
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .offset(x = 128.dp, y = (-710).dp)
                    .width(50.dp)
                    .height(50.dp)
                    .alpha(0f)
            ) {
                Text("")
            }

            Button(
                onClick = {
                    navController.navigate("MedalsPage")
                },
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .offset(x = 189.dp, y = (-715).dp)
                    .width(50.dp)
                    .height(50.dp)
                    .alpha(0f)
            ) {
                Text("")
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalComposeUiApi::class)
@Composable
fun TodoApp(navController: NavController) {
    var newItemText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Initialize Firebase Firestore
    val db = FirebaseFirestore.getInstance()

    // State for storing the list of todo items
    var todoItems by remember { mutableStateOf(listOf<String>()) }

    // Load the background image using the Painter class
    val backgroundImage = painterResource(id = R.drawable.challenge_page)

    // CoroutineScope for launching Firestore queries
    val coroutineScope = rememberCoroutineScope()

    // Load initial TODO items from Firestore and listen for updates
    LaunchedEffect(Unit) {
        val collectionRef = db.collection("todoItems")

        // Function to fetch and update items
        fun fetchAndUpdateItems() {
            coroutineScope.launch {
                val querySnapshot = collectionRef.get().await()
                val items = mutableListOf<String>()
                for (document in querySnapshot.documents) {
                    val item = document.getString("item")
                    item?.let { items.add(it) }
                }
                todoItems = items
            }
        }

        // Initial fetch
        fetchAndUpdateItems()

        // Listen for updates (real-time)
        collectionRef.addSnapshotListener { _, _ ->
            // When changes occur, fetch and update items again
            fetchAndUpdateItems()
        }


    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = backgroundImage,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()

        ) {
            Text(
                text = "",
                style = TextStyle(fontSize = 24.sp),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Add a text input field for adding new to-do items
            BasicTextField(
                value = newItemText,
                onValueChange = {
                    newItemText = it
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (newItemText.isNotBlank()) {
                            // Add the new item to Firestore
                            val newItem = hashMapOf("item" to newItemText)
                            db.collection("todoItems")
                                .add(newItem)
                                .addOnSuccessListener {
                                    // Clear the text input field
                                    newItemText = ""
                                    keyboardController?.hide()
                                }
                                .addOnFailureListener { e ->
                                    Log.w("Firestore", "Error adding document", e)
                                }
                        }
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Cyan.copy(alpha = 0.3f))
                    .padding(bottom = 5.dp),
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 25.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
            )

            // Box for displaying to-do items with removal button
            TodoList(todoItems, db)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    Button(
        onClick = { navController.navigate("ProfilePage") },
        modifier = Modifier
            .size(width = 165.dp, height = 50.dp)
            .offset(y = (770).dp, x = (125).dp)
            .alpha(0f)
    ) {

    }
}

@Composable
// Create a LazyColumn to display the list of to-do items
fun TodoList(items: List<String>, db: FirebaseFirestore) {

    Box(
        modifier = Modifier

    ) {
        LazyColumn(
            modifier = Modifier.height(600.dp)
        ) {
            items(items) { item ->
                TodoItem(item, db)
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
// Composable for rendering individual to-do items
fun TodoItem(item: String, db: FirebaseFirestore) {
    var isDeletionInProgress by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Firebase Init
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    val showDialog = remember { mutableStateOf(false) }

    // Use LaunchedEffect to observe changes in isDeletionInProgress
    LaunchedEffect(isDeletionInProgress) {
        if (isDeletionInProgress) {
            // Cool Down for the Cooldown function
            delay(3000)

            // Show the dialog only when deletion is in progress
            showDialog.value = true

            // After the cooldown, perform the deletion and reset flags
            if (userId != null) {
                grantXP(userId)
            }
            deleteDataFromFirestore(item, db, context)

            isDeletionInProgress = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .border(3.dp, Color.Magenta, shape = CircleShape)
            .background(Color(0x80FFFFFF), shape = CircleShape)
            .clickable {
                // Handle regular click action here
                if (!isDeletionInProgress) {
                    showDialog.value = true
                }
            }
    ) {
        if (isDeletionInProgress) {
            // Display the deletion in progress message
            Text(
                text = "Deleting...",
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center)
                    .background(Color.Magenta),
                style = TextStyle(
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold
                )
            )
        } else {
            // Display the regular item text
            Text(
                text = item,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center),
                style = TextStyle(
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
            },
            title = {
                Text("Complete Challenge")
            },
            text = {
                Text("Do you want to complete this Challenge?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Handle completion action here
                        if (userId != null) {
                            grantXP(userId)
                        }
                        deleteDataFromFirestore(item, db, context)
                        isDeletionInProgress = false
                        showDialog.value = false
                    }
                ) {
                    Text("Complete!!")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        // Handle dismissal action here
                        isDeletionInProgress = false
                        showDialog.value = false
                    }
                ) {
                    Text("Cancel...")
                }
            }
        )
    }
}


// Function to delete a to-do item from Firestore
private fun deleteDataFromFirestore(item: String, db: FirebaseFirestore, context: Context) {

    // Create a query to find documents in the
    // "todoItems" collection where the "item" field matches the given item.
    db.collection("todoItems")
        .whereEqualTo("item", item)
        .get()
        .addOnSuccessListener { documents ->
            // Loop through the documents that match the query.
            for (document in documents) {
                db.collection("todoItems")
                    .document(document.id)
                    .delete()
                    .addOnSuccessListener {
                        // Display a toast message when the to do item is deleted.
                        Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnFailureListener {
                        // Display a toast message when deletion fails.
                        Toast.makeText(context, "Failed to Delete", Toast.LENGTH_SHORT)
                            .show()
                    }
            }
        }
}