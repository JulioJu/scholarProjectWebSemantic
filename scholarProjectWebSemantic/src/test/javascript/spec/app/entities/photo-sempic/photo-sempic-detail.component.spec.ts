/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ScholarProjectWebSemanticTestModule } from '../../../test.module';
import { PhotoSempicDetailComponent } from 'app/entities/photo-sempic/photo-sempic-detail.component';
import { PhotoSempic } from 'app/shared/model/photo-sempic.model';

describe('Component Tests', () => {
  describe('PhotoSempic Management Detail Component', () => {
    let comp: PhotoSempicDetailComponent;
    let fixture: ComponentFixture<PhotoSempicDetailComponent>;
    const route = ({ data: of({ photo: new PhotoSempic(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ScholarProjectWebSemanticTestModule],
        declarations: [PhotoSempicDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(PhotoSempicDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(PhotoSempicDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.photo).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
