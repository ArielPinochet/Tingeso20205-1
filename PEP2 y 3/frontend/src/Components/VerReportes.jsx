import React, { useState, useEffect } from "react";
import axios from "axios";

const VerReportes = () => {
  const [selectedReport, setSelectedReport] = useState("VUELTAS");
  const [startMonth, setStartMonth] = useState("");
  const [endMonth, setEndMonth] = useState("");
  const [cargando, setCargando] = useState(false);
  const [errorServicio, setErrorServicio] = useState(false);

  // Verifica el servicio apenas se entra a la página
  useEffect(() => {
    const checkServicio = async () => {
      try {
        // Prueba con un endpoint de reportes (puede ser HEAD o GET)
        await axios.get("http://localhost:8080/api/reportes/estado");
        setErrorServicio(false);
      } catch (error) {
        setErrorServicio(true);
      }
    };
    checkServicio();
  }, []);

  const descargarExcel = async () => {
    if (!startMonth || !endMonth) {
      alert("Por favor, seleccione el mes de inicio y el de fin.");
      return;
    }
    setCargando(true);
    setErrorServicio(false);
    try {
      let url = "";
      let params = {};
      let filename = "";

      if (selectedReport === "VUELTAS") {
        url = "http://localhost:8080/api/reportes/descargarExcelVueltas";
        params = {
          inicio: startMonth,
          fin: endMonth
        };
        filename = `Reporte_VUELTAS_${startMonth}_a_${endMonth}.xlsx`;
      } else if (selectedReport === "PERSONAS") {
        url = "http://localhost:8080/api/reportes/descargarExcelPersonas";
        params = {
          inicio: startMonth,
          fin: endMonth
        };
        filename = `Reporte_PERSONAS_${startMonth}_a_${endMonth}.xlsx`;
      }

      const response = await axios.get(url, {
        params,
        responseType: "blob"
      });
      const blobUrl = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement("a");
      link.href = blobUrl;
      link.setAttribute("download", filename);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      setErrorServicio(true);
      console.error(error);
    }
    setCargando(false);
  };

  return (
    <div className="container mt-4">
      <h2>Descargar Reporte de Ingresos</h2>
      {errorServicio && (
        <div className="alert alert-danger text-center" style={{ fontSize: "1.1rem" }}>
          Servicio de reportes temporalmente fuera de servicio, inténtelo más tarde.
        </div>
      )}
      <div>
        <button
          onClick={() => setSelectedReport("VUELTAS")}
          style={{
            backgroundColor:
              selectedReport === "VUELTAS" ? "#007bff" : "#e0e0e0",
            color: selectedReport === "VUELTAS" ? "#fff" : "#000",
            marginRight: "10px",
            border: "none",
            padding: "10px 20px",
            borderRadius: "5px",
            fontWeight: "bold"
          }}
          disabled={cargando || errorServicio}
        >
          Reporte por Vueltas
        </button>
        <button
          onClick={() => setSelectedReport("PERSONAS")}
          style={{
            backgroundColor:
              selectedReport === "PERSONAS" ? "#007bff" : "#e0e0e0",
            color: selectedReport === "PERSONAS" ? "#fff" : "#000",
            border: "none",
            padding: "10px 20px",
            borderRadius: "5px",
            fontWeight: "bold"
          }}
          disabled={cargando || errorServicio}
        >
          Reporte por Personas
        </button>
      </div>
      <div className="mt-3">
        <label>
          Mes de Inicio:{" "}
          <input
            type="month"
            value={startMonth}
            onChange={(e) => setStartMonth(e.target.value)}
            disabled={cargando || errorServicio}
          />
        </label>
        &nbsp;&nbsp;
        <label>
          Mes de Fin:{" "}
          <input
            type="month"
            value={endMonth}
            onChange={(e) => setEndMonth(e.target.value)}
            disabled={cargando || errorServicio}
          />
        </label>
      </div>
      <div className="mt-4">
        <button
          onClick={descargarExcel}
          className="btn btn-success"
          style={{ fontWeight: "bold", fontSize: "16px" }}
          disabled={cargando || errorServicio}
        >
          {cargando ? (
            <>
              <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              Descargando...
            </>
          ) : (
            "Descargar Reporte en Excel"
          )}
        </button>
      </div>
    </div>
  );
};

export default VerReportes;
