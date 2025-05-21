import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Entreprise e2e test', () => {
  const entreprisePageUrl = '/entreprise';
  const entreprisePageUrlPattern = new RegExp('/entreprise(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const entrepriseSample = {};

  let entreprise;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/entreprises+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/entreprises').as('postEntityRequest');
    cy.intercept('DELETE', '/api/entreprises/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (entreprise) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/entreprises/${entreprise.id}`,
      }).then(() => {
        entreprise = undefined;
      });
    }
  });

  it('Entreprises menu should load Entreprises page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('entreprise');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Entreprise').should('exist');
    cy.url().should('match', entreprisePageUrlPattern);
  });

  describe('Entreprise page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(entreprisePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Entreprise page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/entreprise/new$'));
        cy.getEntityCreateUpdateHeading('Entreprise');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', entreprisePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/entreprises',
          body: entrepriseSample,
        }).then(({ body }) => {
          entreprise = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/entreprises+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [entreprise],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(entreprisePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Entreprise page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('entreprise');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', entreprisePageUrlPattern);
      });

      it('edit button click should load edit Entreprise page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Entreprise');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', entreprisePageUrlPattern);
      });

      it('edit button click should load edit Entreprise page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Entreprise');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', entreprisePageUrlPattern);
      });

      it('last delete button click should delete instance of Entreprise', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('entreprise').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', entreprisePageUrlPattern);

        entreprise = undefined;
      });
    });
  });

  describe('new Entreprise page', () => {
    beforeEach(() => {
      cy.visit(`${entreprisePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Entreprise');
    });

    it('should create an instance of Entreprise', () => {
      cy.get(`[data-cy="nom_etp"]`).type('drive').should('have.value', 'drive');

      cy.get(`[data-cy="pays"]`).type('morph').should('have.value', 'morph');

      cy.get(`[data-cy="telephone"]`).type('24198').should('have.value', '24198');

      cy.get(`[data-cy="description"]`).type('Soap Buckinghamshire').should('have.value', 'Soap Buckinghamshire');

      cy.get(`[data-cy="deviseSize"]`).type('12662').should('have.value', '12662');

      cy.get(`[data-cy="devise"]`).type('Forward Web').should('have.value', 'Forward Web');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        entreprise = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', entreprisePageUrlPattern);
    });
  });
});
