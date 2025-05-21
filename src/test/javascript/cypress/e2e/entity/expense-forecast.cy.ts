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

describe('ExpenseForecast e2e test', () => {
  const expenseForecastPageUrl = '/expense-forecast';
  const expenseForecastPageUrlPattern = new RegExp('/expense-forecast(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const expenseForecastSample = {};

  let expenseForecast;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/expense-forecasts+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/expense-forecasts').as('postEntityRequest');
    cy.intercept('DELETE', '/api/expense-forecasts/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (expenseForecast) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/expense-forecasts/${expenseForecast.id}`,
      }).then(() => {
        expenseForecast = undefined;
      });
    }
  });

  it('ExpenseForecasts menu should load ExpenseForecasts page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('expense-forecast');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ExpenseForecast').should('exist');
    cy.url().should('match', expenseForecastPageUrlPattern);
  });

  describe('ExpenseForecast page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(expenseForecastPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ExpenseForecast page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/expense-forecast/new$'));
        cy.getEntityCreateUpdateHeading('ExpenseForecast');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', expenseForecastPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/expense-forecasts',
          body: expenseForecastSample,
        }).then(({ body }) => {
          expenseForecast = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/expense-forecasts+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [expenseForecast],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(expenseForecastPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ExpenseForecast page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('expenseForecast');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', expenseForecastPageUrlPattern);
      });

      it('edit button click should load edit ExpenseForecast page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ExpenseForecast');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', expenseForecastPageUrlPattern);
      });

      it('edit button click should load edit ExpenseForecast page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ExpenseForecast');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', expenseForecastPageUrlPattern);
      });

      it('last delete button click should delete instance of ExpenseForecast', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('expenseForecast').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', expenseForecastPageUrlPattern);

        expenseForecast = undefined;
      });
    });
  });

  describe('new ExpenseForecast page', () => {
    beforeEach(() => {
      cy.visit(`${expenseForecastPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ExpenseForecast');
    });

    it('should create an instance of ExpenseForecast', () => {
      cy.get(`[data-cy="label"]`).type('Concrete').should('have.value', 'Concrete');

      cy.get(`[data-cy="monthlyAmount"]`).type('97373').should('have.value', '97373');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        expenseForecast = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', expenseForecastPageUrlPattern);
    });
  });
});
