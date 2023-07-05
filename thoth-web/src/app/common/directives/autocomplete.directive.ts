import {
  Directive,
  ElementRef,
  EventEmitter,
  HostListener,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output
} from '@angular/core';
import {IonInput, IonSpinner, PopoverController} from "@ionic/angular";

export interface AutocompleteProvider {
  fetch(s: string): Promise<string[]>
}

@Directive({
  selector: '[appAutocomplete]',
})
export class AutocompleteDirective implements OnDestroy {

  @Input() autocompleteProvider: AutocompleteProvider = {
    async fetch(s: string): Promise<string[]> {
      return []
    }
  }

  @Input() db = 500;

  private readonly container: HTMLIonCardContentElement;
  private readonly card: HTMLElement;
  private readonly spinner: HTMLElement;
  private lastLen = 0;
  private lastTimeout: any | undefined;

  private listener = () => {
    this.spinner.style.display = "none";
    this.card.style.display = "none";
  }

  constructor(private el: ElementRef) {
    this.card = document.createElement('ion-card');
    this.card.style.padding = "0";
    this.container = document.createElement('ion-card-content');
    this.container.style.padding = "0";
    this.card.appendChild(this.container);
    this.card.style.display = "none";
    this.spinner = document.createElement('ion-spinner');
    this.spinner.setAttribute('name', 'dots');
    this.spinner.style.position = "absolute";
    this.spinner.style.right = "0";
    this.spinner.style.display = "none";

    el.nativeElement.appendChild(this.spinner);
    document.body.appendChild(this.card);
    document.addEventListener('click', this.listener);
    document.addEventListener('resize', this.listener);
    document.addEventListener('wheel', this.listener);
  }

  @HostListener('keyup')
  private onChange() {
    this.spinner.style.display = "block";
    if (this.lastTimeout) {
      clearTimeout(this.lastTimeout);
    }
    this.lastTimeout = setTimeout(() => {
      const bb = this.el.nativeElement.getBoundingClientRect();
      this.card.style.width = bb.width + "px";
      this.card.style.top = (bb.y + bb.height) + "px";
      this.card.style.left = bb.x + "px";
      const txt = this.el.nativeElement.value;
      this.autocompleteProvider.fetch(txt).then(resp => {
        if (resp.length) {
          this.card.style.display = "block";
        } else {
          this.card.style.display = "none";
          return;
        }
        if (this.lastLen == resp.length) {
          return;
        }
        this.lastLen = resp.length;
        this.container.innerHTML = '';
        for (let string of resp) {
          const i = document.createElement('ion-item');
          i.setAttribute('lines', 'none');
          i.onclick = () => {
            this.el.nativeElement.value = string;
          };
          i.setAttribute('button', 'true')
          const l = document.createElement('ion-label');
          i.appendChild(l);
          l.innerText = string;
          this.container.appendChild(i);
        }
      }).finally(() => {
        this.spinner.style.display = "none";
      });
    }, this.db);

  }

  ngOnDestroy(): void {
    this.container.remove();
    this.spinner.remove();
    document.removeEventListener('click', this.listener);
    document.removeEventListener('resize', this.listener);
    document.removeEventListener('wheel', this.listener);
  }


}
