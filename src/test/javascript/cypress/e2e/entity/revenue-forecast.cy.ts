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

describe('RevenueForecast e2e test', () => {
  const revenueForecastPageUrl = '/revenue-forecast';
  const revenueForecastPageUrlPattern = new RegExp('/revenue-forecast(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const revenueForecastSample = {};

  let revenueForecast;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/revenue-forecasts+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/revenue-forecasts').as('postEntityRequest');
    cy.intercept('DELETE', '/api/revenue-forecasts/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (revenueForecast) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/revenue-forecasts/${revenueForecast.id}`,
      }).then(() => {
        revenueForecast = undefined;
      });
    }
  });

  it('RevenueForecasts menu should load RevenueForecasts page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('revenue-forecast');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('RevenueForecast').should('exist');
    cy.url().should('match', revenueForecastPageUrlPattern);
  });

  describe('RevenueForecast page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(revenueForecastPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create RevenueForecast page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/revenue-forecast/new$'));
        cy.getEntityCreateUpdateHeading('RevenueForecast');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', revenueForecastPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/revenue-forecasts',
          body: revenueForecastSample,
        }).then(({ body }) => {
          revenueForecast = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/revenue-forecasts+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [revenueForecast],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(revenueForecastPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details RevenueForecast page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('revenueForecast');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', revenueForecastPageUrlPattern);
      });

      it('edit button click should load edit RevenueForecast page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('RevenueForecast');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', revenueForecastPageUrlPattern);
      });

      it('edit button click should load edit RevenueForecast page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('RevenueForecast');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', revenueForecastPageUrlPattern);
      });

      it('last delete button click should delete instance of RevenueForecast', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('revenueForecast').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', revenueForecastPageUrlPattern);

        revenueForecast = undefined;
      });
    });
  });

  describe('new RevenueForecast page', () => {
    beforeEach(() => {
      cy.visit(`${revenueForecastPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('RevenueForecast');
    });

    it('should create an instance of RevenueForecast', () => {
      cy.get(`[data-cy="month"]`).type('65646').should('have.value', '65646');

      cy.get(`[data-cy="year"]`).type('91154').should('have.value', '91154');

      cy.get(`[data-cy="unitsSold"]`).type('16940').should('have.value', '16940');

      cy.get(`[data-cy="totalRevenue"]`).type('70753').should('have.value', '70753');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        revenueForecast = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', revenueForecastPageUrlPattern);
    });
  });
});
