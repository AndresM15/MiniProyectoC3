import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Ventana {
    private static final String XML_FILE = "productos.xml";
    private static final String JSON_FILE = "facturas.json";

    public static void main(String[] args) {
        // Crear el marco principal
        JFrame frame = new JFrame("Gestión de Productos");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(null);

        // Etiquetas y campos de texto
        JLabel lblCodigo = new JLabel("Código:");
        lblCodigo.setBounds(20, 20, 80, 25);
        JTextField txtCodigo = new JTextField();
        txtCodigo.setBounds(100, 20, 150, 25);

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(20, 60, 80, 25);
        JTextField txtNombre = new JTextField();
        txtNombre.setBounds(100, 60, 150, 25);

        JLabel lblPrecio = new JLabel("Precio:");
        lblPrecio.setBounds(20, 100, 80, 25);
        JTextField txtPrecio = new JTextField();
        txtPrecio.setBounds(100, 100, 150, 25);

        JLabel lblCategoria = new JLabel("Categoría:");
        lblCategoria.setBounds(20, 140, 80, 25);
        JTextField txtCategoria = new JTextField();
        txtCategoria.setBounds(100, 140, 150, 25);

        // Botones
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(300, 20, 100, 30);

        JButton btnModificar = new JButton("Modificar");
        btnModificar.setBounds(300, 60, 100, 30);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(300, 100, 100, 30);
        

        JButton btnComprar = new JButton("Comprar");
        btnComprar.setBounds(300, 140, 100, 30);

        JButton btnVerFacturas = new JButton("Ver Facturas");
        btnVerFacturas.setBounds(420, 140, 150, 30);

        // Tabla
        String[] columnNames = {"Código", "Nombre", "Precio", "Categoría"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 200, 540, 150);

        // Añadir componentes al marco
        frame.add(lblCodigo);
        frame.add(txtCodigo);
        frame.add(lblNombre);
        frame.add(txtNombre);
        frame.add(lblPrecio);
        frame.add(txtPrecio);
        frame.add(lblCategoria);
        frame.add(txtCategoria);
        frame.add(btnGuardar);
        frame.add(btnModificar);
        frame.add(btnEliminar);
        frame.add(btnComprar);
        frame.add(btnVerFacturas);
        frame.add(scrollPane);

        // Cargar datos desde XML
        cargarDesdeXML(tableModel);

        // Acciones de los botones
        btnGuardar.addActionListener(e -> {
            String codigo = txtCodigo.getText();
            String nombre = txtNombre.getText();
            String precio = txtPrecio.getText();
            String categoria = txtCategoria.getText();

            if (codigo.isEmpty() || nombre.isEmpty() || precio.isEmpty() || categoria.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Todos los campos son obligatorios.");
                return;
            }

            // Agregar a la tabla
            tableModel.addRow(new Object[]{codigo, nombre, precio, categoria});

            // Guardar en XML
            guardarEnXML(tableModel);

            // Limpiar campos
            txtCodigo.setText("");
            txtNombre.setText("");
            txtPrecio.setText("");
            txtCategoria.setText("");
        });

        btnModificar.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Seleccione un producto para modificar.");
                return;
            }

            tableModel.setValueAt(txtCodigo.getText(), selectedRow, 0);
            tableModel.setValueAt(txtNombre.getText(), selectedRow, 1);
            tableModel.setValueAt(txtPrecio.getText(), selectedRow, 2);
            tableModel.setValueAt(txtCategoria.getText(), selectedRow, 3);

            // Guardar en XML
            guardarEnXML(tableModel);
        });

        btnEliminar.addActionListener(e -> {
            try {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            throw new ProductoNoEncontradoException("No se encontró el producto para eliminar.");
        }

        tableModel.removeRow(selectedRow);
        guardarEnXML(tableModel);
    } catch (ProductoNoEncontradoException ex) {
        JOptionPane.showMessageDialog(frame, ex.getMessage());
    }
        });

        btnComprar.addActionListener(e -> {
            JFrame ventanaFactura = crearVentanaFactura();
            ventanaFactura.setVisible(true);
        });

        btnVerFacturas.addActionListener(e -> {
            JFrame ventanaFacturas = crearVentanaFacturas();
            ventanaFacturas.setVisible(true);
        });

        // Hacer visible el marco
        frame.setVisible(true);
    }

    private static void guardarEnXML(DefaultTableModel tableModel) {
        try (PrintWriter writer = new PrintWriter(new File(XML_FILE))) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                StringBuilder producto = new StringBuilder();
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    producto.append(tableModel.getValueAt(i, j).toString());
                    if (j < tableModel.getColumnCount() - 1) {
                        producto.append(",");
                    }
                }
                writer.println(producto);
            }
        } catch (FileNotFoundException ex) {
            System.err.println("El archivo no se encontró: " + ex.getMessage());
        }
    }

    private static void cargarDesdeXML(DefaultTableModel tableModel) {
        File file = new File(XML_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                tableModel.addRow(values);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            System.err.println("Error al leer el archivo XML: " + e.getMessage());
        }
    }

    private static JFrame crearVentanaFactura() {
        JFrame ventanaFactura = new JFrame("Registrar Factura");
        ventanaFactura.setSize(400, 500);
        ventanaFactura.setLayout(null);

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(20, 20, 100, 25);
        JTextField txtNombre = new JTextField();
        txtNombre.setBounds(150, 20, 200, 25);

        JLabel lblId = new JLabel("Identificación:");
        lblId.setBounds(20, 60, 100, 25);
        JTextField txtId = new JTextField();
        txtId.setBounds(150, 60, 200, 25);

        JLabel lblDireccion = new JLabel("Dirección:");
        lblDireccion.setBounds(20, 100, 100, 25);
        JTextField txtDireccion = new JTextField();
        txtDireccion.setBounds(150, 100, 200, 25);

        JLabel lblProducto = new JLabel("Producto:");
        lblProducto.setBounds(20, 140, 100, 25);
        JTextField txtProducto = new JTextField();
        txtProducto.setBounds(150, 140, 200, 25);

        JLabel lblImpuesto = new JLabel("Impuesto:");
        lblImpuesto.setBounds(20, 180, 100, 25);
        JTextField txtImpuesto = new JTextField();
        txtImpuesto.setBounds(150, 180, 200, 25);

        JLabel lblTotal = new JLabel("Total:");
        lblTotal.setBounds(20, 220, 100, 25);
        JTextField txtTotal = new JTextField();
        txtTotal.setBounds(150, 220, 200, 25);

        JButton btnGuardarFactura = new JButton("Guardar Factura");
        btnGuardarFactura.setBounds(100, 300, 200, 30);
        ventanaFactura.add(btnGuardarFactura);

        ventanaFactura.add(lblNombre);
        ventanaFactura.add(txtNombre);
        ventanaFactura.add(lblId);
        ventanaFactura.add(txtId);
        ventanaFactura.add(lblDireccion);
        ventanaFactura.add(txtDireccion);
        ventanaFactura.add(lblProducto);
        ventanaFactura.add(txtProducto);
        ventanaFactura.add(lblImpuesto);
        ventanaFactura.add(txtImpuesto);
        ventanaFactura.add(lblTotal);
        ventanaFactura.add(txtTotal);

        btnGuardarFactura.addActionListener(e -> {
    // Código para guardar datos

            String nombre = txtNombre.getText();
            String id = txtId.getText();
            String direccion = txtDireccion.getText();
            String producto = txtProducto.getText();
            String impuesto = txtImpuesto.getText();
            String total = txtTotal.getText();

            if (nombre.isEmpty() || id.isEmpty() || direccion.isEmpty() || producto.isEmpty() || impuesto.isEmpty() || total.isEmpty()) {
                JOptionPane.showMessageDialog(ventanaFactura, "Todos los campos son obligatorios.");
                return;
            }

            Map<String, String> factura = new HashMap<>();
            factura.put("nombre", nombre);
            factura.put("id", id);
            factura.put("direccion", direccion);
            factura.put("producto", producto);
            factura.put("impuesto", impuesto);
            factura.put("total", total);

            guardarFacturaEnJSON(factura);
            ventanaFactura.dispose();
            if (nombre.isEmpty() || txtId.getText().isEmpty()) {
                try {
                    throw new FacturaInvalidaException("Los datos de la factura no son válidos.");
                } catch (FacturaInvalidaException ex) {
                    Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
                }
}
        });

        return ventanaFactura;
    }

    private static void guardarFacturaEnJSON(Map<String, String> factura) {
        Gson gson = new Gson();
        List<Map<String, String>> facturas = new ArrayList<>();

        File file = new File(JSON_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                Type listType = new TypeToken<List<Map<String, String>>>() {}.getType();
                facturas = gson.fromJson(reader, listType);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        facturas.add(factura);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            gson.toJson(facturas, writer);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static JFrame crearVentanaFacturas() {
        JFrame ventanaFacturas = new JFrame("Lista de Facturas");
        ventanaFacturas.setSize(500, 400);
        ventanaFacturas.setLayout(null);

        String[] columnNames = {"Nombre", "Identificación", "Dirección", "Producto", "Impuesto", "Total"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 20, 440, 300);
        ventanaFacturas.add(scrollPane);

        cargarFacturasDesdeJSON(tableModel);

        return ventanaFacturas;
    }

    private static void cargarFacturasDesdeJSON(DefaultTableModel tableModel) {
        File file = new File(JSON_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Map<String, String>>>() {}.getType();
            List<Map<String, String>> facturas = gson.fromJson(reader, listType);

            for (Map<String, String> factura : facturas) {
                tableModel.addRow(new Object[]{
                    factura.get("nombre"),
                    factura.get("id"),
                    factura.get("direccion"),
                    factura.get("producto"),
                    factura.get("impuesto"),
                    factura.get("total")
                });
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
    
}

class ProductoNoEncontradoException extends Exception {
    public ProductoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}

class FacturaInvalidaException extends Exception {
    public FacturaInvalidaException(String mensaje) {
        super(mensaje);
    }
}

class ArchivoCorruptoException extends Exception {
    public ArchivoCorruptoException(String mensaje) {
        super(mensaje);
    }
}
