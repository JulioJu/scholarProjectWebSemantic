/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ScholarProjectWebSemanticTestModule } from '../../../test.module';
import { AlbumSempicDetailComponent } from 'app/entities/album-sempic/album-sempic-detail.component';
import { AlbumSempic } from 'app/shared/model/album-sempic.model';

describe('Component Tests', () => {
  describe('AlbumSempic Management Detail Component', () => {
    let comp: AlbumSempicDetailComponent;
    let fixture: ComponentFixture<AlbumSempicDetailComponent>;
    const route = ({ data: of({ album: new AlbumSempic(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ScholarProjectWebSemanticTestModule],
        declarations: [AlbumSempicDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(AlbumSempicDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(AlbumSempicDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.album).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
