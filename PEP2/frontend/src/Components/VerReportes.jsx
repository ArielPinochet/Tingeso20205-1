import React, { useState } from "react";
import axios from "axios";
import { jsPDF } from "jspdf";

const VerReportes = () => {
  // Estado para controlar el reporte seleccionado
  // "vueltas", "tiempo" y "personas" (también podemos tener "total" para ver solo el total de cada mes, pero en este ejemplo nos enfocamos en el detalle)
  const [selectedReport, setSelectedReport] = useState("vueltas");

  // Estados para el filtro de meses (se usan en todos los reportes para un filtrado homogéneo)
  const [startMonth, setStartMonth] = useState("");
  const [endMonth, setEndMonth] = useState("");

  // Estados para almacenar los datos retornados por el backend (detalle mes a mes)
  const [reportesVueltas, setReportesVueltas] = useState([]);
  const [reportesTiempo, setReportesTiempo] = useState([]);
  const [reportesPersonas, setReportesPersonas] = useState([]);

  // Estado para almacenar el total de ingresos (suma de todos los registros retornados)
  const [totalIngresos, setTotalIngresos] = useState(0);

  // Función para consultar el backend según el tipo de reporte seleccionado
  const fetchReporte = () => {
    if (!startMonth || !endMonth) {
      alert("Por favor, seleccione el mes de inicio y el de fin.");
      return;
    }

    // Mapeo de endpoints según el reporte
    const endpointMap = {
      vueltas: "vueltas/mes",
      tiempo: "tiempo/mes",
      personas: "personas/mes"
    };

    const API_URL = process.env.REACT_APP_API_URL || "http://localhost:8090";
    axios
      .get(
        `${API_URL}/reservas/${endpointMap[selectedReport]}?start=${startMonth}&end=${endMonth}`
      )
      .then((response) => {
        console.log(`Datos para ${selectedReport}:`, response.data);
        // Dependiendo del reporte, guardamos en el estado correspondiente
        if (selectedReport === "vueltas") {
          setReportesVueltas(response.data);
        } else if (selectedReport === "tiempo") {
          setReportesTiempo(response.data);
        } else if (selectedReport === "personas") {
          setReportesPersonas(response.data);
        }
        // Calculamos el total de ingresos (suma de todos los ingresos de la respuesta)
        const total = response.data.reduce(
          (acc, item) => acc + Number(item.ingresos),
          0
        );
        setTotalIngresos(total);
      })
      .catch((error) =>
        console.error("Error al obtener reporte (detalle mes a mes):", error)
      );
  };

  // Función para generar y descargar el PDF del reporte mostrado
  const generatePdf = () => {
    const doc = new jsPDF();
    let y = 20;
    
    doc.setFontSize(16);
    doc.text("Reporte de Ingresos", 20, y);
    y += 10;
    doc.setFontSize(12);
    doc.text(`Rango de Meses: ${startMonth} a ${endMonth}`, 20, y);
    y += 10;
    
    let header = "";
    let reportData = [];
    if (selectedReport === "vueltas") {
      header = "Detalle por Número de Vueltas";
      reportData = reportesVueltas;
    } else if (selectedReport === "tiempo") {
      header = "Detalle por Tiempo (min)";
      reportData = reportesTiempo;
    } else if (selectedReport === "personas") {
      header = "Detalle por Número de Personas";
      reportData = reportesPersonas;
    }
    doc.text(header, 20, y);
    y += 10;
    
    reportData.forEach((r) => {
      let line = "";
      if (selectedReport === "vueltas") {
        // Se muestra el mes, la cantidad de vueltas y los ingresos en ese detalle
        line = `Mes: ${r.mes} | Vueltas: ${r.numeroVueltas} | Ingresos: $${r.ingresos}`;
      } else if (selectedReport === "tiempo") {
        line = `Mes: ${r.mes} | Duración: ${r.duracionTotal} min | Ingresos: $${r.ingresos}`;
      } else if (selectedReport === "personas") {
        line = `Mes: ${r.mes} | Categoría: ${r.categoria} | Ingresos: $${r.ingresos}`;
      }
      doc.text(line, 20, y);
      y += 10;
    });
    
    doc.text(`Total Ingresos: $${totalIngresos}`, 20, y);
    
    const now = new Date();
    const fileName = `Reporte_${selectedReport}_${now.toISOString().slice(0,10)}.pdf`;
    doc.save(fileName);
  };

  // Función para renderizar la tabla según el reporte seleccionado (detalle mes a mes)
  const renderTable = () => {
    let data = [];
    let columns = [];
    
    if (selectedReport === "vueltas") {
      data = reportesVueltas;
      columns = ["Mes", "Número de Vueltas", "Ingresos"];
    } else if (selectedReport === "tiempo") {
      data = reportesTiempo;
      columns = ["Mes", "Duración (min)", "Ingresos"];
    } else if (selectedReport === "personas") {
      data = reportesPersonas;
      columns = ["Mes", "Categoría", "Ingresos"];
    }
    
    if (data.length === 0) return <p>No hay datos para mostrar.</p>;
    
    return (
      <table border="1">
        <thead>
          <tr>
            {columns.map((col, idx) => (
              <th key={idx}>{col}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {data.map((r, idx) => (
            <tr key={idx}>
              <td>{r.mes}</td>
              {selectedReport === "vueltas" && <td>{r.numeroVueltas}</td>}
              {selectedReport === "tiempo" && <td>{r.duracionTotal}</td>}
              {selectedReport === "personas" && <td>{r.categoria}</td>}
              <td>{r.ingresos}</td>
            </tr>
          ))}
        </tbody>
      </table>
    );
  };

  return (
    <div className="container mt-4">
      <h2>Reportes de Ingresos (Detalle Mes a Mes)</h2>
      <div>
        <button onClick={() => setSelectedReport("vueltas")}>Por Vueltas</button>
        <button onClick={() => setSelectedReport("tiempo")}>Por Tiempo</button>
        <button onClick={() => setSelectedReport("personas")}>Por Personas</button>
      </div>
      <div className="mt-3">
        <label>
          Mes de Inicio:{" "}
          <input type="month" value={startMonth} onChange={(e) => setStartMonth(e.target.value)} />
        </label>
        &nbsp;&nbsp;
        <label>
          Mes de Fin:{" "}
          <input type="month" value={endMonth} onChange={(e) => setEndMonth(e.target.value)} />
        </label>
        &nbsp;&nbsp;
        <button onClick={fetchReporte}>Generar Reporte</button>
      </div>
      <div className="mt-3">{renderTable()}</div>
      <div className="mt-3">
        <button onClick={generatePdf}>Descargar Reporte en PDF</button>
      </div>
    </div>
  );
};

export default VerReportes;
