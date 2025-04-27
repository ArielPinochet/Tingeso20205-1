import React, { useState, useEffect } from "react";
import axios from "axios";
import { jsPDF } from "jspdf";

const VerReportes = () => {
  // Estados para reportes sin filtro por mes (los que se cargan al montar el componente)
  const [reportesVueltas, setReportesVueltas] = useState([]);
  const [reportesTiempo, setReportesTiempo] = useState([]);
  const [reportesPersonas, setReportesPersonas] = useState([]);
  
  // Estados para el reporte con filtro “de mes a mes”
  const [reportesMes, setReportesMes] = useState([]);
  const [totalIngresosMes, setTotalIngresosMes] = useState(0);
  const [startMonth, setStartMonth] = useState("");
  const [endMonth, setEndMonth] = useState("");
  
  // Estado para controlar el reporte a mostrar:
  // "vueltas", "tiempo", "personas" o "mes"
  const [selectedReport, setSelectedReport] = useState("vueltas");

  // Cargar datos de las otras secciones (sin filtro por mes) al montar el componente.
  useEffect(() => {
    // Estos endpoints podrían mostrarse por defecto en la sección "vueltas", "tiempo" y "personas"
    axios
      .get("http://localhost:8080/reservas/vueltas")
      .then((response) => {
        const data = response.data;
        console.log("Datos reportesVueltas:", data);
        setReportesVueltas(Array.isArray(data) ? data : [data]);
      })
      .catch((error) =>
        console.error("Error al obtener reporte por vueltas:", error)
      );

    axios
      .get("http://localhost:8080/reservas/tiempo")
      .then((response) => {
        const data = response.data;
        console.log("Datos reportesTiempo:", data);
        setReportesTiempo(Array.isArray(data) ? data : [data]);
      })
      .catch((error) =>
        console.error("Error al obtener reporte por tiempo:", error)
      );

    axios
      .get("http://localhost:8080/reservas/personas")
      .then((response) => {
        const data = response.data;
        console.log("Datos reportesPersonas:", data);
        setReportesPersonas(Array.isArray(data) ? data : [data]);
      })
      .catch((error) =>
        console.error("Error al obtener reporte por personas:", error)
      );
  }, []);

  // Función para cargar el reporte filtrado "de mes a mes"
  const fetchReporteMes = () => {
    if (!startMonth || !endMonth) {
      alert("Por favor, seleccione el mes de inicio y el mes de fin.");
      return;
    }
    axios
      .get(`http://localhost:8080/reservas/mes?start=${startMonth}&end=${endMonth}`)
      .then((response) => {
        console.log("Datos reporteMes:", response.data);
        setReportesMes(response.data);
        const total = response.data.reduce(
          (acc, item) => acc + Number(item.ingresos),
          0
        );
        setTotalIngresosMes(total);
      })
      .catch((error) => {
        console.error("Error al obtener reporte por mes:", error);
      });
  };

  // Función para generar y descargar el PDF
  const generatePdf = () => {
    const doc = new jsPDF();
    let y = 20;
    
    if (selectedReport === "mes") {
      // Reporte PDF para la consulta "de mes a mes"
      doc.setFontSize(16);
      doc.text("Reporte de Ingresos por Mes", 20, y);
      y += 10;
      doc.setFontSize(12);
      if (reportesMes.length > 0) {
        reportesMes.forEach((item) => {
          doc.text(`Mes: ${item.mes}  |  Ingresos: $${item.ingresos}`, 20, y);
          y += 10;
        });
        y += 10;
        doc.setFontSize(14);
        doc.text(`Total Ingresos: $${totalIngresosMes}`, 20, y);
      } else {
        doc.text("No se encontraron datos para el rango seleccionado.", 20, y);
      }
      const now = new Date();
      const day = String(now.getDate()).padStart(2, "0");
      const mon = String(now.getMonth() + 1).padStart(2, "0");
      const year = now.getFullYear();
      const hour = String(now.getHours()).padStart(2, "0");
      const minute = String(now.getMinutes()).padStart(2, "0");
      const fileName = `Reporte_Ingresos_Mes_${day}${mon}${year}_${hour}${minute}.pdf`;
      doc.save(fileName);
    } else {
      // Reporte PDF para las otras secciones
      doc.setFontSize(16);
      doc.text("Reporte de Ingresos", 20, y);
      y += 10;
  
      if (selectedReport === "vueltas") {
        doc.setFontSize(14);
        doc.text("Reporte por Número de Vueltas", 20, y);
        y += 10;
        doc.setFontSize(12);
        if (Array.isArray(reportesVueltas) && reportesVueltas.length > 0) {
          reportesVueltas.forEach((r) => {
            doc.text(`Vueltas: ${r.numeroVueltas} | Ingresos: $${r.ingresos}`, 20, y);
            y += 10;
          });
        } else {
          doc.text("No hay datos para este reporte", 20, y);
          y += 10;
        }
      } else if (selectedReport === "tiempo") {
        doc.setFontSize(14);
        doc.text("Reporte por Tiempo (min)", 20, y);
        y += 10;
        doc.setFontSize(12);
        if (Array.isArray(reportesTiempo) && reportesTiempo.length > 0) {
          reportesTiempo.forEach((r) => {
            doc.text(`Tiempo: ${r.duracionTotal} min | Ingresos: $${r.ingresos}`, 20, y);
            y += 10;
          });
        } else {
          doc.text("No hay datos para este reporte", 20, y);
          y += 10;
        }
      } else if (selectedReport === "personas") {
        doc.setFontSize(14);
        doc.text("Reporte por Número de Personas", 20, y);
        y += 10;
        doc.setFontSize(12);
        if (Array.isArray(reportesPersonas) && reportesPersonas.length > 0) {
          reportesPersonas.forEach((r) => {
            doc.text(`Categoría: ${r.categoria} | Ingresos: $${r.ingresos}`, 20, y);
            y += 10;
          });
        } else {
          doc.text("No hay datos para este reporte", 20, y);
          y += 10;
        }
      }
  
      const now = new Date();
      const day = String(now.getDate()).padStart(2, "0");
      const mon = String(now.getMonth() + 1).padStart(2, "0");
      const year = now.getFullYear();
      const hour = String(now.getHours()).padStart(2, "0");
      const minute = String(now.getMinutes()).padStart(2, "0");
      const fileName = `Reporte_Ingresos_${selectedReport}_${day}${mon}${year}_${hour}${minute}.pdf`;
      doc.save(fileName);
    }
  };

  // Renderiza la tabla según el tipo de reporte
  const renderTable = () => {
    if (selectedReport === "mes") {
      return (
        <div>
          <div>
            <label>
              Mes de Inicio:{" "}
              <input
                type="month"
                value={startMonth}
                onChange={(e) => setStartMonth(e.target.value)}
              />
            </label>
            &nbsp;&nbsp;
            <label>
              Mes de Fin:{" "}
              <input
                type="month"
                value={endMonth}
                onChange={(e) => setEndMonth(e.target.value)}
              />
            </label>
            &nbsp;&nbsp;
            <button onClick={fetchReporteMes}>Generar Reporte</button>
          </div>
          {reportesMes.length > 0 ? (
            <div className="mt-3">
              <table border="1">
                <thead>
                  <tr>
                    <th>Mes</th>
                    <th>Ingresos</th>
                  </tr>
                </thead>
                <tbody>
                  {reportesMes.map((item, idx) => (
                    <tr key={idx}>
                      <td>{item.mes}</td>
                      <td>{item.ingresos}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
              <h4 className="mt-3">Total Ingresos: ${totalIngresosMes}</h4>
            </div>
          ) : (
            <p className="mt-3">No hay datos para mostrar.</p>
          )}
        </div>
      );
    } else if (selectedReport === "vueltas") {
      return (
        <table border="1">
          <thead>
            <tr>
              <th>Número de Vueltas</th>
              <th>Ingresos</th>
            </tr>
          </thead>
          <tbody>
            {reportesVueltas.map((r, index) => (
              <tr key={index}>
                <td>{r.numeroVueltas}</td>
                <td>{r.ingresos}</td>
              </tr>
            ))}
          </tbody>
        </table>
      );
    } else if (selectedReport === "tiempo") {
      return (
        <table border="1">
          <thead>
            <tr>
              <th>Duración (min)</th>
              <th>Ingresos</th>
            </tr>
          </thead>
          <tbody>
            {reportesTiempo.map((r, index) => (
              <tr key={index}>
                <td>{r.duracionTotal}</td>
                <td>{r.ingresos}</td>
              </tr>
            ))}
          </tbody>
        </table>
      );
    } else if (selectedReport === "personas") {
      return (
        <table border="1">
          <thead>
            <tr>
              <th>Categoría</th>
              <th>Ingresos</th>
            </tr>
          </thead>
          <tbody>
            {reportesPersonas.map((r, index) => (
              <tr key={index}>
                <td>{r.categoria}</td>
                <td>{r.ingresos}</td>
              </tr>
            ))}
          </tbody>
        </table>
      );
    }
  };

  return (
    <div className="container mt-4">
      <h2>Reportes de Ingresos</h2>
      <div>
        <button onClick={() => setSelectedReport("vueltas")}>Por Vueltas</button>
        <button onClick={() => setSelectedReport("tiempo")}>Por Tiempo</button>
        <button onClick={() => setSelectedReport("personas")}>Por Personas</button>
        <button onClick={() => setSelectedReport("mes")}>Por Mes</button>
      </div>
      <div className="mt-3">{renderTable()}</div>
      <div className="mt-3">
        <button onClick={generatePdf}>Descargar Reporte en PDF</button>
      </div>
    </div>
  );
};

export default VerReportes;
