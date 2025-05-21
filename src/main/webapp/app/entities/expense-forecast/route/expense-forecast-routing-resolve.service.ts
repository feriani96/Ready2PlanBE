import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IExpenseForecast } from '../expense-forecast.model';
import { ExpenseForecastService } from '../service/expense-forecast.service';

@Injectable({ providedIn: 'root' })
export class ExpenseForecastRoutingResolveService implements Resolve<IExpenseForecast | null> {
  constructor(protected service: ExpenseForecastService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IExpenseForecast | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((expenseForecast: HttpResponse<IExpenseForecast>) => {
          if (expenseForecast.body) {
            return of(expenseForecast.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
