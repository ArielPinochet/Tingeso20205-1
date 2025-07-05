import React from "react";

const Ayuda = () => (
  <div className="container py-5 text-center">
    <h2 className="mb-4" style={{ color: "#1976d2", fontWeight: 700 }}>
      Página de Ayuda
    </h2>
    <div className="alert alert-info" style={{ fontSize: "1.15rem" }}>
      Esta sección está en desarrollo.<br />
      Pronto encontrarás aquí ejemplos y guías para usar la aplicación.
    </div>
    <img
      src="https://cdn-icons-png.flaticon.com/512/595/595067.png"
      alt="En desarrollo"
      style={{ width: 90, marginTop: 24, opacity: 0.7 }}
    />
  </div>
);

export default Ayuda;