import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { catchError, forkJoin, map, of } from 'rxjs';

type Empleado = {
  clave: string;
  nombre: string;
  direccion: string;
  telefono: string;
};

type Departamento = {
  id: number;
  nombre: string;
  descripcion: string | null;
  activo: boolean;
};

type EmpleadoDepartamentoActual = {
  empleadoClave: string;
  departamentoId: number;
  departamentoNombre: string;
  fechaInicio: string;
};

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  correo = '';
  contrasena = '';
  rolUsuario = '';
  estado = 'Listo para iniciar sesion';
  token = '';
  crudEstado = '';
  deptoEstado = '';

  empleados: Empleado[] = [];
  departamentos: Departamento[] = [];
  creando = false;
  creandoDepto = false;
  includeInactiveDepartamentos = false;

  nuevoNombre = '';
  nuevaDireccion = '';
  nuevoTelefono = '';

  editClave = '';
  editNombre = '';
  editDireccion = '';
  editTelefono = '';
  destinoDepartamentoPorEmpleado: Record<string, string> = {};
  nombreDepartamentoPorEmpleado: Record<string, string> = {};

  nuevoDepartamentoNombre = '';
  nuevoDepartamentoDescripcion = '';
  editDepartamentoId: number | null = null;
  editDepartamentoNombre = '';
  editDepartamentoDescripcion = '';

  constructor(private readonly http: HttpClient) {}

  get esAdmin(): boolean {
    return this.rolUsuario === 'ADMIN';
  }

  iniciarSesion(): void {
    this.estado = 'Autenticando...';
    this.token = '';

    this.http
      .post<{ token: string; role: string }>('/auth/login', {
        usuario: this.correo,
        contrasena: this.contrasena
      })
      .subscribe({
        next: (response: { token: string; role: string }) => {
          this.token = response.token;
          this.rolUsuario = response.role;
          this.estado = 'Login correcto';
          this.cargarDepartamentos();
          this.cargarEmpleados();
        },
        error: (error: { error?: { message?: string } }) => {
          const mensaje = error?.error?.message ?? 'No fue posible autenticar';
          this.estado = `Error: ${mensaje}`;
        }
      });
  }

  cerrarSesion(): void {
    this.token = '';
    this.rolUsuario = '';
    this.empleados = [];
    this.editClave = '';
    this.crudEstado = '';
    this.departamentos = [];
    this.destinoDepartamentoPorEmpleado = {};
    this.nombreDepartamentoPorEmpleado = {};
    this.deptoEstado = '';
    this.editDepartamentoId = null;
    this.estado = 'Sesion cerrada';
  }

  cargarEmpleados(): void {
    if (!this.token) {
      return;
    }

    this.crudEstado = 'Cargando empleados...';
    this.http
      .get<Empleado[]>('/api/empleados', {
        headers: {
          Authorization: `Bearer ${this.token}`
        }
      })
      .subscribe({
        next: (data: Empleado[]) => {
          this.empleados = data;
          const siguientesDestinos: Record<string, string> = {};
          for (const empleado of data) {
            siguientesDestinos[empleado.clave] = this.destinoDepartamentoPorEmpleado[empleado.clave] ?? '';
          }
          this.destinoDepartamentoPorEmpleado = siguientesDestinos;
          this.cargarAsignacionesActuales(data);
          this.crudEstado = `Empleados cargados: ${data.length}`;
        },
        error: (error: { error?: { message?: string } }) => {
          const mensaje = error?.error?.message ?? 'No fue posible cargar empleados';
          this.crudEstado = `Error: ${mensaje}`;
        }
      });
  }

  private cargarAsignacionesActuales(empleados: Empleado[]): void {
    if (!this.token || empleados.length === 0) {
      return;
    }

    const headers = {
      Authorization: `Bearer ${this.token}`
    };

    const consultas = empleados.map((empleado) =>
      this.http.get<EmpleadoDepartamentoActual>(`/api/empleados/${empleado.clave}/departamento`, { headers }).pipe(
        map((asignacion: EmpleadoDepartamentoActual) => ({
          clave: empleado.clave,
          departamentoId: String(asignacion.departamentoId),
          departamentoNombre: asignacion.departamentoNombre
        })),
        catchError((error: { status?: number }) => {
          if (error?.status === 404) {
            return of({ clave: empleado.clave, departamentoId: '', departamentoNombre: 'Sin departamento' });
          }

          return of({ clave: empleado.clave, departamentoId: '', departamentoNombre: 'Sin departamento' });
        })
      )
    );

    forkJoin(consultas).subscribe({
      next: (asignaciones: Array<{ clave: string; departamentoId: string; departamentoNombre: string }>) => {
        const siguientesDestinos: Record<string, string> = { ...this.destinoDepartamentoPorEmpleado };
        const siguientesNombres: Record<string, string> = { ...this.nombreDepartamentoPorEmpleado };
        for (const asignacion of asignaciones) {
          siguientesDestinos[asignacion.clave] = asignacion.departamentoId;
          siguientesNombres[asignacion.clave] = asignacion.departamentoNombre;
        }
        this.destinoDepartamentoPorEmpleado = siguientesDestinos;
        this.nombreDepartamentoPorEmpleado = siguientesNombres;
      }
    });
  }

  nombreDepartamentoDeEmpleado(claveEmpleado: string): string {
    const nombre = this.nombreDepartamentoPorEmpleado[claveEmpleado];
    if (nombre && nombre.trim().length > 0) {
      return nombre;
    }

    return 'Sin departamento';
  }

  crearEmpleado(): void {
    if (!this.token || this.creando) {
      return;
    }

    this.creando = true;
    this.crudEstado = 'Creando empleado...';

    this.http
      .post<Empleado>(
        '/api/empleados',
        {
          nombre: this.nuevoNombre,
          direccion: this.nuevaDireccion,
          telefono: this.nuevoTelefono
        },
        {
          headers: {
            Authorization: `Bearer ${this.token}`
          }
        }
      )
      .subscribe({
        next: () => {
          this.nuevoNombre = '';
          this.nuevaDireccion = '';
          this.nuevoTelefono = '';
          this.crudEstado = 'Empleado creado correctamente';
          this.creando = false;
          this.cargarEmpleados();
        },
        error: (error: { error?: { message?: string } }) => {
          const mensaje = error?.error?.message ?? 'No fue posible crear el empleado';
          this.crudEstado = `Error: ${mensaje}`;
          this.creando = false;
        }
      });
  }

  seleccionarEdicion(empleado: Empleado): void {
    this.editClave = empleado.clave;
    this.editNombre = empleado.nombre;
    this.editDireccion = empleado.direccion;
    this.editTelefono = empleado.telefono;
  }

  cancelarEdicion(): void {
    this.editClave = '';
    this.editNombre = '';
    this.editDireccion = '';
    this.editTelefono = '';
  }

  guardarEdicion(): void {
    if (!this.token || !this.editClave) {
      return;
    }

    this.crudEstado = `Actualizando ${this.editClave}...`;
    this.http
      .put<Empleado>(
        `/api/empleados/${this.editClave}`,
        {
          nombre: this.editNombre,
          direccion: this.editDireccion,
          telefono: this.editTelefono
        },
        {
          headers: {
            Authorization: `Bearer ${this.token}`
          }
        }
      )
      .subscribe({
        next: () => {
          this.crudEstado = 'Empleado actualizado correctamente';
          this.cancelarEdicion();
          this.cargarEmpleados();
        },
        error: (error: { error?: { message?: string } }) => {
          const mensaje = error?.error?.message ?? 'No fue posible actualizar el empleado';
          this.crudEstado = `Error: ${mensaje}`;
        }
      });
  }

  eliminarEmpleado(clave: string): void {
    if (!this.token) {
      return;
    }

    this.crudEstado = `Eliminando ${clave}...`;
    this.http
      .delete(`/api/empleados/${clave}`, {
        headers: {
          Authorization: `Bearer ${this.token}`
        }
      })
      .subscribe({
        next: () => {
          this.crudEstado = 'Empleado eliminado correctamente';
          this.cargarEmpleados();
        },
        error: (error: { status?: number; error?: { message?: string } }) => {
          if (this.manejarUnauthorized(error) || this.manejarForbidden(error)) {
            return;
          }
          const mensaje = error?.error?.message ?? 'No fue posible eliminar el empleado';
          this.crudEstado = `Error: ${mensaje}`;
        }
      });
  }

  cargarDepartamentos(): void {
    if (!this.token) {
      return;
    }

    this.deptoEstado = 'Cargando departamentos...';
    const includeInactive = this.includeInactiveDepartamentos ? 'true' : 'false';

    this.http
      .get<Departamento[]>(`/api/departamentos?includeInactive=${includeInactive}`, {
        headers: {
          Authorization: `Bearer ${this.token}`
        }
      })
      .subscribe({
        next: (data: Departamento[]) => {
          this.departamentos = data;
          this.deptoEstado = `Departamentos cargados: ${data.length}`;
        },
        error: (error: { status?: number; error?: { message?: string } }) => {
          if (this.manejarUnauthorized(error)) {
            return;
          }
          if (this.manejarForbidden(error)) {
            return;
          }
          const mensaje = error?.error?.message ?? 'No fue posible cargar departamentos';
          this.deptoEstado = `Error: ${mensaje}`;
        }
      });
  }

  crearDepartamento(): void {
    if (!this.token || this.creandoDepto) {
      return;
    }

    this.creandoDepto = true;
    this.deptoEstado = 'Creando departamento...';

    this.http
      .post<Departamento>(
        '/api/departamentos',
        {
          nombre: this.nuevoDepartamentoNombre,
          descripcion: this.nuevoDepartamentoDescripcion
        },
        {
          headers: {
            Authorization: `Bearer ${this.token}`
          }
        }
      )
      .subscribe({
        next: () => {
          this.nuevoDepartamentoNombre = '';
          this.nuevoDepartamentoDescripcion = '';
          this.deptoEstado = 'Departamento creado correctamente';
          this.creandoDepto = false;
          this.cargarDepartamentos();
        },
        error: (error: { status?: number; error?: { message?: string } }) => {
          if (this.manejarUnauthorized(error)) {
            this.creandoDepto = false;
            return;
          }
          if (this.manejarForbidden(error)) {
            this.creandoDepto = false;
            return;
          }
          const mensaje = error?.error?.message ?? 'No fue posible crear el departamento';
          this.deptoEstado = `Error: ${mensaje}`;
          this.creandoDepto = false;
        }
      });
  }

  seleccionarEdicionDepartamento(departamento: Departamento): void {
    this.editDepartamentoId = departamento.id;
    this.editDepartamentoNombre = departamento.nombre;
    this.editDepartamentoDescripcion = departamento.descripcion ?? '';
  }

  cancelarEdicionDepartamento(): void {
    this.editDepartamentoId = null;
    this.editDepartamentoNombre = '';
    this.editDepartamentoDescripcion = '';
  }

  guardarEdicionDepartamento(): void {
    if (!this.token || this.editDepartamentoId === null) {
      return;
    }

    this.deptoEstado = `Actualizando departamento ${this.editDepartamentoId}...`;
    this.http
      .put<Departamento>(
        `/api/departamentos/${this.editDepartamentoId}`,
        {
          nombre: this.editDepartamentoNombre,
          descripcion: this.editDepartamentoDescripcion
        },
        {
          headers: {
            Authorization: `Bearer ${this.token}`
          }
        }
      )
      .subscribe({
        next: () => {
          this.deptoEstado = 'Departamento actualizado correctamente';
          this.cancelarEdicionDepartamento();
          this.cargarDepartamentos();
        },
        error: (error: { status?: number; error?: { message?: string } }) => {
          if (this.manejarUnauthorized(error)) {
            return;
          }
          if (this.manejarForbidden(error)) {
            return;
          }
          const mensaje = error?.error?.message ?? 'No fue posible actualizar el departamento';
          this.deptoEstado = `Error: ${mensaje}`;
        }
      });
  }

  inactivarDepartamento(departamentoId: number): void {
    if (!this.token) {
      return;
    }

    this.deptoEstado = `Inactivando departamento ${departamentoId}...`;
    this.http
      .delete(`/api/departamentos/${departamentoId}`, {
        headers: {
          Authorization: `Bearer ${this.token}`
        }
      })
      .subscribe({
        next: () => {
          this.deptoEstado = 'Departamento inactivado correctamente';
          this.cargarDepartamentos();
        },
        error: (error: { status?: number; error?: { message?: string } }) => {
          if (this.manejarUnauthorized(error)) {
            return;
          }
          if (this.manejarForbidden(error)) {
            return;
          }
          const mensaje = error?.error?.message ?? 'No fue posible inactivar el departamento';
          this.deptoEstado = `Error: ${mensaje}`;
        }
      });
  }

  asignarDepartamentoEmpleado(claveEmpleado: string): void {
    if (!this.token) {
      return;
    }

    const destinoRaw = this.destinoDepartamentoPorEmpleado[claveEmpleado];
    const departamentoId = Number(destinoRaw);
    if (!destinoRaw || Number.isNaN(departamentoId) || departamentoId <= 0) {
      this.crudEstado = 'Selecciona un departamento activo para asignar';
      return;
    }

    this.crudEstado = `Asignando departamento a ${claveEmpleado}...`;
    this.http
      .put(
        `/api/empleados/${claveEmpleado}/departamento`,
        { departamentoId },
        {
          headers: {
            Authorization: `Bearer ${this.token}`
          }
        }
      )
      .subscribe({
        next: () => {
          this.cargarAsignacionesActuales(this.empleados);
          this.crudEstado = `Departamento asignado a ${claveEmpleado}`;
        },
        error: (error: { status?: number; error?: { message?: string } }) => {
          if (this.manejarUnauthorized(error)) {
            return;
          }
          if (this.manejarForbidden(error)) {
            return;
          }
          if (error?.status === 409) {
            const mensajeConflicto =
              error?.error?.message ?? 'Conflicto concurrente al asignar departamento. Intenta nuevamente';
            this.crudEstado = `Error: ${mensajeConflicto}`;
            return;
          }
          const mensaje = error?.error?.message ?? 'No fue posible asignar el departamento';
          this.crudEstado = `Error: ${mensaje}`;
        }
      });
  }

  activarDepartamento(departamentoId: number): void {
    if (!this.token) {
      return;
    }

    this.deptoEstado = `Activando departamento ${departamentoId}...`;
    this.http
      .post(
        `/api/departamentos/${departamentoId}/activar`,
        {},
        {
          headers: {
            Authorization: `Bearer ${this.token}`
          }
        }
      )
      .subscribe({
        next: () => {
          this.deptoEstado = 'Departamento activado correctamente';
          this.cargarDepartamentos();
        },
        error: (error: { status?: number; error?: { message?: string } }) => {
          if (this.manejarUnauthorized(error)) {
            return;
          }
          if (this.manejarForbidden(error)) {
            return;
          }
          const mensaje = error?.error?.message ?? 'No fue posible activar el departamento';
          this.deptoEstado = `Error: ${mensaje}`;
        }
      });
  }

  private manejarUnauthorized(error: { status?: number }): boolean {
    if (error?.status !== 401) {
      return false;
    }

    this.crudEstado = 'Sesion invalida o expirada. Inicia sesion nuevamente';
    this.deptoEstado = 'Sesion invalida o expirada. Inicia sesion nuevamente';
    this.estado = 'Sesion invalida o expirada. Inicia sesion nuevamente';
    return true;
  }

  private manejarForbidden(error: { status?: number }): boolean {
    if (error?.status !== 403) {
      return false;
    }

    this.crudEstado = 'No autorizado para realizar esta accion';
    this.deptoEstado = 'No autorizado para administrar departamentos';
    this.cancelarEdicionDepartamento();
    return true;
  }
}
