import React from "react";
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import ListaClientes from "./Components/ClientList";
import FormularioCliente from "./Components/FormClients";
import ListaCarros from "./Components/ListaCarros";
import FormularioCarro from "./Components/FormularioCarro";
import ListaComprobantes from "./Components/ListaComprobantes";
import FormularioComprobante from "./Components/FormularioComprobante";
import VerComprobante from "./Components/VerComprobante";
import ListaReservas from "./Components/ListaReservas";
import FormularioReserva from "./Components/FormularioReserva";
import FormularioPago from "./Components/FormularioPago";
import Calendario from "./Components/Calendario";
import VerReportes from "./Components/VerReportes";
import PaginaPrincipal from "./Components/PaginaPrincipal";
import Ayuda from "./Components/Ayuda"; // Nuevo componente de ayuda

import logo2 from "./assets/logo2.png";

import "bootstrap/dist/css/bootstrap.min.css";

const App = () => {
  return (
    <Router>
      <div
        className="container mt-4"
        style={{
          position: "relative",
          minHeight: "100vh",
          paddingBottom: "120px", // Más espacio para ayuda y footer
        }}
      >
        <nav className="navbar navbar-expand-lg navbar-dark bg-primary">
          <div className="container">
            <Link className="navbar-brand d-flex align-items-center" to="/">
              <img
                src={logo2}
                alt="Logo"
                style={{ height: "40px", marginRight: "10px" }}
              />
              KartingRM
            </Link>
            <button
              className="navbar-toggler"
              type="button"
              data-bs-toggle="collapse"
              data-bs-target="#navbarNav"
            >
              <span className="navbar-toggler-icon"></span>
            </button>
            <div className="collapse navbar-collapse" id="navbarNav">
              <ul className="navbar-nav ms-auto">
                <li className="nav-item">
                  <Link className="nav-link" to="/clientes">
                    Clientes
                  </Link>
                </li>
                <li className="nav-item">
                  <Link className="nav-link" to="/carros">
                    Carros
                  </Link>
                </li>
                <li className="nav-item">
                  <Link className="nav-link" to="/comprobantes">
                    Comprobantes
                  </Link>
                </li>
                <li className="nav-item">
                  <Link className="nav-link" to="/reservas">
                    Reservas
                  </Link>
                </li>
                <li className="nav-item">
                  <Link className="nav-link" to="/Calendario">
                    Calendario
                  </Link>
                </li>
                <li className="nav-item">
                  <Link className="nav-link" to="/VerReportes">
                    Reportes
                  </Link>
                </li>
                {/* Pestaña destacada para agendar reserva */}
                <li className="nav-item">
                  <Link
                    className="nav-link btn btn-warning text-dark fw-bold ms-2 px-3 py-1"
                    style={{ borderRadius: "20px" }}
                    to="/crear-reserva"
                  >
                    AGENDAR RESERVA
                  </Link>
                </li>
              </ul>
            </div>
          </div>
        </nav>

        <div className="mt-4">
          <Routes>
            <Route path="/clientes" element={<ListaClientes />} />
            <Route path="/crear-cliente" element={<FormularioCliente />} />
            <Route path="/editar-cliente/:id" element={<FormularioCliente />} />
            <Route path="/" element={<PaginaPrincipal />} /> {/* Página principal */}

            <Route path="/carros" element={<ListaCarros />} />
            <Route path="/crear-carro" element={<FormularioCarro />} />
            <Route path="/editar-carro/:id" element={<FormularioCarro />} />

            <Route path="/comprobantes" element={<ListaComprobantes />} />
            <Route path="/crear-comprobante/:id" element={<FormularioComprobante />} />
            <Route path="/ver-comprobante/:id" element={<VerComprobante />} />
            
            <Route path="/reservas" element={<ListaReservas />} />
            <Route path="/crear-reserva" element={<FormularioReserva />} />
            <Route path="/editar-reserva/:id" element={<FormularioReserva />} />
            <Route path="/pagar-reserva/:id" element={<FormularioPago />} />

            {/* Agregamos la ruta para el Calendario */}
            <Route path="/Calendario" element={<Calendario />} />
            <Route path="/VerReportes" element={<VerReportes />} />
            <Route path="/ayuda" element={<Ayuda />} /> {/* Ruta de ayuda */}
          </Routes>
        </div>

        {/* Botón de ayuda flotante abajo a la derecha */}
        <Link
          to="/ayuda"
          style={{
            position: "fixed",
            right: 28,
            bottom: 80,
            zIndex: 10000,
            background: "#1976d2",
            color: "#fff",
            borderRadius: "30px",
            width: "110px",
            height: "48px",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            boxShadow: "0 2px 12px rgba(25,118,210,0.18)",
            fontSize: 20,
            textDecoration: "none",
            border: "3px solid #fff",
            transition: "background 0.2s",
            fontWeight: 600,
            letterSpacing: "1px"
          }}
          title="Ir a la ayuda"
        >
          <span style={{ color: "#fff" }}>Ayuda</span>
        </Link>

        {/* Sección de ayuda siempre visible abajo */}
        <div
          style={{
            position: "fixed",
            left: 0,
            bottom: 60,
            width: "100vw",
            background: "#f8fafc",
            color: "#1976d2",
            textAlign: "center",
            padding: "16px 0 8px 0",
            fontWeight: 500,
            fontSize: "1.05rem",
            zIndex: 9998,
            borderTop: "1px solid #e3e3e3",
          }}
        >
          {/* Sección de ayuda eliminada según solicitud */}
        </div>

        {/* Footer horario fijo abajo */}
        <footer
          style={{
            position: "fixed",
            left: 0,
            bottom: 0,
            width: "100vw",
            background: "#1976d2",
            color: "#fff",
            textAlign: "center",
            padding: "12px 0",
            fontWeight: 500,
            letterSpacing: "1px",
            zIndex: 9999,
          }}
        >
          Horario de atención: 06:00 a 23:00 hrs
        </footer>
      </div>
    </Router>
  );
};

export default App;
