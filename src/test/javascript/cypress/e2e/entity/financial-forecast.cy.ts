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

describe('FinancialForecast e2e test', () => {
  const financialForecastPageUrl = '/financial-forecast';
  const financialForecastPageUrlPattern = new RegExp('/financial-forecast(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const financialForecastSample = {};

  let financialForecast;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/financial-forecasts+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/financial-forecasts').as('postEntityRequest');
    cy.intercept('DELETE', '/api/financial-forecasts/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (financialForecast) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/financial-forecasts/${financialForecast.id}`,
      }).then(() => {
        financialForecast = undefined;
      });
    }
  });

  it('FinancialForecasts menu should load FinancialForecasts page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('financial-forecast');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('FinancialForecast').should('exist');
    cy.url().should('match', financialForecastPageUrlPattern);
  });

  describe('FinancialForecast page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(financialForecastPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create FinancialForecast page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/financial-forecast/new$'));
        cy.getEntityCreateUpdateHeading('FinancialForecast');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', financialForecastPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/financial-forecasts',
          body: financialForecastSample,
        }).then(({ body }) => {
          financialForecast = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/financial-forecasts+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [financialForecast],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(financialForecastPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details FinancialForecast page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('financialForecast');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', financialForecastPageUrlPattern);
      });

      it('edit button click should load edit FinancialForecast page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('FinancialForecast');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', financialForecastPageUrlPattern);
      });

      it('edit button click should load edit FinancialForecast page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('FinancialForecast');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', financialForecastPageUrlPattern);
      });

      it('last delete button click should delete instance of FinancialForecast', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('financialForecast').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', financialForecastPageUrlPattern);

        financialForecast = undefined;
      });
    });
  });

  describe('new FinancialForecast page', () => {
    beforeEach(() => {
      cy.visit(`${financialForecastPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('FinancialForecast');
    });

    it('should create an instance of FinancialForecast', () => {
      cy.get(`[data-cy="startDate"]`).type('18874').should('have.value', '18874');

      cy.get(`[data-cy="durationInMonths"]`).type('13601').should('have.value', '13601');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        financialForecast = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', financialForecastPageUrlPattern);
    });
  });
});
