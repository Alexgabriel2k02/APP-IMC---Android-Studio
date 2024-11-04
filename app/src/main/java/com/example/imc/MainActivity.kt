package com.example.imc

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.app_imc.R
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var editTextWeight: EditText
    private lateinit var editTextHeight: EditText
    private lateinit var textViewResult: TextView
    private lateinit var bmiDao: BmiDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicialização dos elementos do layout
        editTextWeight = findViewById(R.id.etPeso)
        editTextHeight = findViewById(R.id.etAltura)
        textViewResult = findViewById(R.id.tvResultado)
        val buttonCalculate: Button = findViewById(R.id.button2)
        val buttonSave: Button = findViewById(R.id.buttonSave)
        val buttonLoad: Button = findViewById(R.id.buttonLoad)
        val buttonUpdate: Button = findViewById(R.id.buttonUpdate)
        val buttonDelete: Button = findViewById(R.id.buttonDelete)

        // Inicialização do banco de dados
        val db = BmiDataBase.getDatabase(this)
        bmiDao = db.bmiDao()

        // Configuração dos botões
        buttonCalculate.setOnClickListener { calculateBMI() }
        buttonSave.setOnClickListener { saveBMI() }
        buttonLoad.setOnClickListener { loadAllBMIEntries() }
        buttonUpdate.setOnClickListener { updateBMIEntry() }
        buttonDelete.setOnClickListener { deleteBMIEntry() }

        // Configuração de margens para a janela
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun calculateBMI() {
        val weightStr = editTextWeight.text.toString().trim().replace(',', '.')
        val heightStr = editTextHeight.text.toString().trim().replace(',', '.')

        val weight = weightStr.toDoubleOrNull()
        val height = heightStr.toDoubleOrNull()

        if (weight != null && height != null && height > 0) {
            val bmi = weight / (height * height)
            textViewResult.text = String.format("IMC: %.2f", bmi)
        } else {
            textViewResult.text = "Preencha todos os campos com valores válidos."
        }
    }

    private fun saveBMI() {
        val bmiStr = textViewResult.text.toString()

        // Verifique se o IMC foi calculado e exibido antes de salvar
        if (!bmiStr.startsWith("IMC:")) {
            textViewResult.text = "Erro: Calcule o IMC antes de salvar."
            return
        }

        val bmiValue = bmiStr.substringAfter("IMC: ").toDoubleOrNull()
        val heightInput = editTextHeight.text.toString().trim().replace(',', '.')
        val weightInput = editTextWeight.text.toString().trim().replace(',', '.')

        val height = heightInput.toDoubleOrNull()
        val weight = weightInput.toDoubleOrNull()

        // Verifique se os valores de IMC, altura e peso são válidos antes de salvar
        if (bmiValue != null && height != null && weight != null) {
            val bmiEntry = Bmi(bmi = bmiValue, height = height, weight = weight)

            lifecycleScope.launch {
                try {
                    bmiDao.insert(bmiEntry)
                    textViewResult.text = "IMC salvo com sucesso!"
                } catch (e: Exception) {
                    textViewResult.text = "Erro ao salvar IMC: ${e.message}"
                }
            }
        } else {
            textViewResult.text = "Erro ao salvar IMC: campos inválidos."
        }
    }

    private fun loadAllBMIEntries() {
        lifecycleScope.launch {
            val bmiList = bmiDao.getAllBmiEntries()
            if (bmiList.isNotEmpty()) {
                val resultText = bmiList.joinToString(separator = "\n") { entry ->
                    "IMC: %.2f | Altura: %.2f | Peso: %.2f".format(entry.bmi, entry.height, entry.weight)
                }
                textViewResult.text = resultText
            } else {
                textViewResult.text = "Nenhum IMC encontrado."
            }
        }
    }

    private fun updateBMIEntry() {
        val weightStr = editTextWeight.text.toString().trim().replace(',', '.')
        val heightStr = editTextHeight.text.toString().trim().replace(',', '.')

        val weight = weightStr.toDoubleOrNull()
        val height = heightStr.toDoubleOrNull()

        if (weight != null && height != null) {
            lifecycleScope.launch {
                val bmiEntry = bmiDao.getAllBmiEntries().find { it.weight == weight && it.height == height }
                if (bmiEntry != null) {
                    val updatedBmiValue = weight / (height * height)
                    val updatedBmiEntry = bmiEntry.copy(bmi = updatedBmiValue, height = height, weight = weight)

                    try {
                        bmiDao.updateBmi(updatedBmiEntry)
                        textViewResult.text = "IMC atualizado com sucesso!"
                    } catch (e: Exception) {
                        textViewResult.text = "Erro ao atualizar IMC: ${e.message}"
                    }
                } else {
                    textViewResult.text = "Nenhum IMC encontrado para atualizar."
                }
            }
        } else {
            textViewResult.text = "Preencha todos os campos com valores válidos."
        }
    }

    private fun deleteBMIEntry() {
        val weightStr = editTextWeight.text.toString().trim().replace(',', '.')
        val heightStr = editTextHeight.text.toString().trim().replace(',', '.')

        val weight = weightStr.toDoubleOrNull()
        val height = heightStr.toDoubleOrNull()

        if (weight != null && height != null) {
            lifecycleScope.launch {
                val bmiEntry = bmiDao.getAllBmiEntries().find { it.weight == weight && it.height == height }
                if (bmiEntry != null) {
                    try {
                        bmiDao.deleteBmi(bmiEntry)
                        textViewResult.text = "IMC deletado com sucesso!"
                    } catch (e: Exception) {
                        textViewResult.text = "Erro ao deletar IMC: ${e.message}"
                    }
                } else {
                    textViewResult.text = "Nenhum IMC encontrado para deletar."
                }
            }
        } else {
            textViewResult.text = "Preencha todos os campos com valores válidos."
        }
    }
}

