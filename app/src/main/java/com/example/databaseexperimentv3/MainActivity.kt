package com.example.databaseexperimentv3

import android.content.Context
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
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.databaseexperimentv3.ui.theme.DatabaseExperimentV3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DatabaseExperimentV3Theme {
                // Call the main composable function
                MainContent()
            }
        }
    }
}


@Composable
fun MainContent() {
    // Create a NavController
    val navController = rememberNavController()

    // Initialize Firebase Firestore
    val db = FirebaseFirestore.getInstance()

    // State for storing the list of todo items
    val todoItems by remember { mutableStateOf(listOf<String>()) }

    // Set up navigation
    NavHost(
        navController = navController,
        startDestination = "mainPage"
    ) {
        composable("mainPage") {
            MainPage(navController)
        }
        composable("loginPage") {
            LoginPage()
        }
        composable("TodoList"){
            TodoList(todoItems, db)
        }
        // Add more destinations as needed
    }
}


@OptIn(ExperimentalComposeUiApi::class, ExperimentalComposeUiApi::class)
@Composable
// Initialize Firebase Firestore and load initial data
// Also To Do List top bar display, and box lining
fun TodoApp() {
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
                    .padding(bottom = 16.dp),
                textStyle = TextStyle(
                    color = Color.Red,
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
}

@Composable
fun MainPage(navController: NavController) {

    // Load the background image using the Painter class
    val backgroundImage = painterResource(id = R.drawable.starting_page)

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

        // Add the first button with an offset
        Box(
            modifier = Modifier
                .absoluteOffset(y = (-200).dp, x = (-9).dp)
                .align(Alignment.BottomCenter)
        ) {
            Button(
                onClick = {
                    navController.navigate("loginPage")
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
                    // Define the action to perform when the button is clicked
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
fun LoginPage(){

    // Load the background image using the Painter class
    val backgroundImage = painterResource(id = R.drawable.loginpage)

    // State for the text input in the TextBox
    var playerBoxText by remember { mutableStateOf("") }
    var keyBoxText by remember { mutableStateOf("") }

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
            value = playerBoxText,
            onValueChange = {
                playerBoxText = it
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
            value = keyBoxText,
            onValueChange = {
                keyBoxText = it
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
    }

}
@Composable
fun SignupPage(){

    // Load the background image using the Painter class
    val backgroundImage = painterResource(id = R.drawable.loginpage)

    // State for the text input in the TextBox
    var playerBoxText by remember { mutableStateOf("") }
    var keyBoxText by remember { mutableStateOf("") }

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
            value = playerBoxText,
            onValueChange = {
                playerBoxText = it
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
            value = keyBoxText,
            onValueChange = {
                keyBoxText = it
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
    }

}

@Composable
// Create a LazyColumn to display the list of to-do items
fun TodoList(items: List<String>, db: FirebaseFirestore) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(items) { item ->
            TodoItem(item, db)
        }
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
// Composable for rendering individual to-do items
fun TodoItem(item: String, db: FirebaseFirestore) {
    val isDeletingWarningVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .border(1.dp, Color.Magenta)
            .combinedClickable(
                onClick = {
                    // Handle regular click here
                },
                onLongClick = {
                    // Handle long-press here
                    deleteDataFromFirestore(item, db, context)
                }
            )
    ) {

        // Currently Unused due to various bugs.
        //This displays a deleting warning if a user accidentally taps a TODO.
        if (isDeletingWarningVisible) {
            Text(
                text = "DELETING",
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
                        // Display a toast message when the todo item is deleted.
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