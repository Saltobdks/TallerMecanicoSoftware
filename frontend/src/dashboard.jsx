import React from 'react';
import {createRoot} from 'react-dom/client';
import {Building2, Users, LogOut, LayoutDashboard} from 'lucide-react';
import './customer.css';

function Dashboard(){
  const logout=()=>{localStorage.removeItem('token');location.href='/';};
  return <main className="admin-layout"><aside className="sidebar"><a className="customer-brand" href="/dashboard.html"><Building2/>Motor<span>Flow</span></a><nav><a href="/dashboard.html"><LayoutDashboard/>Inicio</a><a href="/workshop.html"><Building2/>Crear / administrar talleres</a><a href="/customer.html"><Users/>Crear / administrar clientes</a></nav><button onClick={logout}><LogOut/>Cerrar sesión</button></aside><section className="admin-content"><p className="eyebrow">ADMINISTRACIÓN</p><h1>Panel de control</h1><p>Gestiona la operación por taller con acceso de mínimo privilegio.</p><div className="admin-cards"><a href="/workshop.html"><Building2/><b>Talleres</b><span>Alta y administración de sucursales.</span></a><a href="/customer.html"><Users/><b>Clientes</b><span>Alta, consulta, edición y suspensión.</span></a></div></section></main>;
}
createRoot(document.getElementById('root')).render(<Dashboard/>);
