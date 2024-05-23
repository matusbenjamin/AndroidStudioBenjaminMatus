package com.matus.androidstudiobenjaminmatus;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private final Map<String, Boolean> productsSelection = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtener referencia a la base de datos de Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("pedidos");

        // Obtener referencias a los CheckBox y al botón
        CheckBox burgerCheckBox = findViewById(R.id.checkBox);
        CheckBox completoCheckBox = findViewById(R.id.checkBox2);
        CheckBox pizzaCheckBox = findViewById(R.id.checkBox3);
        CheckBox drinksCheckBox = findViewById(R.id.checkBox4);
        Button orderButton = findViewById(R.id.botonAgregar);

        // Agregar listeners a los CheckBox
        burgerCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> productsSelection.put("Hamburguesa", isChecked));
        completoCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> productsSelection.put("Completo", isChecked));
        pizzaCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> productsSelection.put("Pizza", isChecked));
        drinksCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> productsSelection.put("Bebidas", isChecked));

        // Agregar listener al botón "PEDIR"
        orderButton.setOnClickListener(v -> {
            // Verificar si se han seleccionado productos
            if (productsSelection.isEmpty()) {
                Toast.makeText(MainActivity.this, "No se seleccionaron productos", Toast.LENGTH_SHORT).show();
            } else {
                // Crear un nuevo pedido
                String pedidoKey = databaseReference.push().getKey();
                if (pedidoKey != null) {
                    // Guardar los productos seleccionados en el pedido
                    enviarPedidoAFirebase(productsSelection);
                    // Iniciar la actividad MainActivity2
                    Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                    startActivity(intent);
                }
            }

            // Limpiar la selección de productos para el próximo pedido
            productsSelection.clear();
        });
    }

    private void enviarPedidoAFirebase(Map<String, Boolean> productsSelection) {
        String pedidoId = databaseReference.child("pedidos").push().getKey();
        Map<String, Object> pedidoMap = new HashMap<>();

        // Agregar los productos seleccionados al pedido
        for (Map.Entry<String, Boolean> entry : productsSelection.entrySet()) {
            if (entry.getValue()) {
                pedidoMap.put(entry.getKey(), true);
            }
        }

        // Guardar el pedido en la base de datos de Firebase
        databaseReference.child("pedidos").child(pedidoId).setValue(pedidoMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(MainActivity.this, "Pedido agregado exitosamente", Toast.LENGTH_SHORT).show();
                    // Limpiar los CheckBox
                    limpiarCheckBox();
                })
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Error al agregar pedido", Toast.LENGTH_SHORT).show());
    }

    private void limpiarCheckBox() {
        // Limpiar el estado de los CheckBox
        CheckBox burgerCheckBox = findViewById(R.id.checkBox);
        CheckBox completoCheckBox = findViewById(R.id.checkBox2);
        CheckBox pizzaCheckBox = findViewById(R.id.checkBox3);
        CheckBox drinksCheckBox = findViewById(R.id.checkBox4);

        burgerCheckBox.setChecked(false);
        completoCheckBox.setChecked(false);
        pizzaCheckBox.setChecked(false);
        drinksCheckBox.setChecked(false);
    }
}
