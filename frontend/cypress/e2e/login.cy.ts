// @ts-nocheck

describe('Login flow', () => {
  it('authenticates with valid credentials (direct API)', () => {
    cy.request('POST', '/auth/login', {
      usuario: 'admin',
      contrasena: 'admin123'
    }).then((response) => {
      expect(response.status).to.eq(200);
      expect(response.body).to.have.property('token').and.to.be.a('string').and.not.be.empty;
      expect(response.body).to.have.property('role').and.to.eq('ADMIN');
    });
  });
});