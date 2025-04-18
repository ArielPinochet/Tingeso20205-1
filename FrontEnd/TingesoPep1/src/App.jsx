import React from "react";
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import ListaClientes from "./components/ClientList";
import FormularioCliente from "./Components/FormClients";
import "bootstrap/dist/css/bootstrap.min.css";

const App = () => {
    return (
        <Router>
            <div className="container mt-4">
                <nav className="navbar navbar-expand-lg navbar-dark bg-primary">
                    <div className="container">
                        <Link className="navbar-brand" to="/">Gestión de Clientes</Link>
                        <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                            <span className="navbar-toggler-icon"></span>
                        </button>
                        <div className="collapse navbar-collapse" id="navbarNav">
                            <ul className="navbar-nav ms-auto">
                                <li className="nav-item">
                                    <Link className="nav-link" to="/">Lista de Clientes</Link>
                                </li>
                                <li className="nav-item">
                                    <Link className="nav-link" to="/crear">Agregar Cliente</Link>
                                </li>
                            </ul>
                        </div>
                    </div>
                </nav>

                <div className="mt-4">
                    <Routes>
                        <Route path="/" element={<ListaClientes />} />
                        <Route path="/crear" element={<FormularioCliente />} />
                        <Route path="/editar/:id" element={<FormularioCliente />} />
                    </Routes>
                </div>
            </div>
        </Router>
    );
};

export default App;
