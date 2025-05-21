import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'entreprise',
        data: { pageTitle: 'ready2PlanBeApp.entreprise.home.title' },
        loadChildren: () => import('./entreprise/entreprise.module').then(m => m.EntrepriseModule),
      },
      {
        path: 'product-or-service',
        data: { pageTitle: 'ready2PlanBeApp.productOrService.home.title' },
        loadChildren: () => import('./product-or-service/product-or-service.module').then(m => m.ProductOrServiceModule),
      },
      {
        path: 'revenue-forecast',
        data: { pageTitle: 'ready2PlanBeApp.revenueForecast.home.title' },
        loadChildren: () => import('./revenue-forecast/revenue-forecast.module').then(m => m.RevenueForecastModule),
      },
      {
        path: 'expense-forecast',
        data: { pageTitle: 'ready2PlanBeApp.expenseForecast.home.title' },
        loadChildren: () => import('./expense-forecast/expense-forecast.module').then(m => m.ExpenseForecastModule),
      },
      {
        path: 'financial-forecast',
        data: { pageTitle: 'ready2PlanBeApp.financialForecast.home.title' },
        loadChildren: () => import('./financial-forecast/financial-forecast.module').then(m => m.FinancialForecastModule),
      },
      {
        path: 'business-plan',
        data: { pageTitle: 'ready2PlanBeApp.businessPlan.home.title' },
        loadChildren: () => import('./business-plan/business-plan.module').then(m => m.BusinessPlanModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
