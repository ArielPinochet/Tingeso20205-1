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
import "bootstrap/dist/css/bootstrap.min.css";

const App = () => {
    return (
        <Router>
            <div className="container mt-4">
                <nav className="navbar navbar-expand-lg navbar-dark bg-primary">
                    <div className="container">
                        <Link className="navbar-brand" to="/">Gestión de Datos</Link>
                        <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                            <span className="navbar-toggler-icon"></span>
                        </button>
                        <div className="collapse navbar-collapse" id="navbarNav">
                            <ul className="navbar-nav ms-auto">
                                <li className="nav-item">
                                    <Link className="nav-link" to="/clientes">Clientes</Link>
                                </li>
                                <li className="nav-item">
                                    <Link className="nav-link" to="/carros">Carros</Link>
                                </li>
                                <li className="nav-item">
                                    <Link className="nav-link" to="/comprobantes">comprobantes</Link>
                                </li>
                                <li className="nav-item">
                                    <Link className="nav-link" to="/reservas">reservas</Link>
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
                        
                        <Route path="/carros" element={<ListaCarros />} />
                        <Route path="/crear-carro" element={<FormularioCarro />} />
                        <Route path="/editar-carro/:id" element={<FormularioCarro />} />

                        <Route path="/comprobantes" element={<ListaComprobantes />} />
                        <Route path="/crear-comprobante" element={<FormularioComprobante />} />
                        <Route path="/ver-comprobante/:id" element={<VerComprobante />} />
                        <Route path="/" element={<ListaClientes />} /> {/* Página principal con Clientes */}
                        <Route path="/reservas" element={<ListaReservas />} />
                        <Route path="/crear-reserva" element={<FormularioReserva />} />
                        <Route path="/editar-reserva/:id" element={<FormularioReserva />} />
                        <Route path="/" element={<ListaReservas />} />
                    </Routes>
                </div>
            </div>
        </Router>
    );
};

export default App;
