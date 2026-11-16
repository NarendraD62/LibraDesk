import { useQuery } from '@tanstack/react-query';
import { ArrowDownUp, ArrowLeft, ArrowRight, Bell, BookOpen, Bookmark, ChevronDown, Command, FlaskConical, LibraryBig, MapPin, Search, SlidersHorizontal, Sparkles } from 'lucide-react';
import { type FormEvent, useEffect, useMemo, useState } from 'react';
import { searchBooks, suggestBooks, type Book } from './lib/api';

const DAILY_RENT_INR = 5;
type BorrowerDetails = { name: string; mobile: string; days: number };
type BorrowedBook = BorrowerDetails & { id: number; book: Book; borrowedOn: string; dueOn: string; totalRent: number };

export default function App() {
  const [query, setQuery] = useState('');
  const [debouncedQuery, setDebouncedQuery] = useState('');
  const [category, setCategory] = useState('All subjects');
  const [sort, setSort] = useState('Popular');
  const [page, setPage] = useState('Discover');
  const [savedIds, setSavedIds] = useState<number[]>(() => {
    try { const value: unknown = JSON.parse(window.localStorage.getItem('libradesk-saved-books') ?? '[]'); if (!Array.isArray(value) || value.some((id: unknown) => typeof id !== 'number')) return []; return value as number[]; }
    catch { return []; }
  });
  const [selectedBookId, setSelectedBookId] = useState<number | null>(null);
  const [borrowBookId, setBorrowBookId] = useState<number | null>(null);
  const [borrowedBooks, setBorrowedBooks] = useState<BorrowedBook[]>([]);
  const [saveMessage, setSaveMessage] = useState('');
  const { data: books = [], isLoading } = useQuery({ queryKey: ['books', query], queryFn: () => searchBooks(query) });
  useEffect(() => { const timer = window.setTimeout(() => setDebouncedQuery(query), 220); return () => window.clearTimeout(timer); }, [query]);
  const { data: suggestions = [] } = useQuery({ queryKey: ['suggest', debouncedQuery], queryFn: () => suggestBooks(debouncedQuery), enabled: debouncedQuery.length > 1 });
  useEffect(() => { window.localStorage.setItem('libradesk-saved-books', JSON.stringify(savedIds)); }, [savedIds]);
  const categories = ['All subjects', 'Computer Science', 'Literature', 'Science', 'History'];
  const navItems = [{ name: 'Discover', icon: Sparkles }, { name: 'Borrowed books', icon: BookOpen }, { name: 'Saved books', icon: Bookmark }, { name: 'Chemistry Lab', icon: FlaskConical }];
  const shown = useMemo(() => books.filter((book) => page !== 'Saved books' || savedIds.includes(book.id)).filter((book) => page !== 'Discover' || category === 'All subjects' || book.category === category).sort((a, b) => sort === 'Title A–Z' ? a.title.localeCompare(b.title) : b.popularity - a.popularity), [books, category, page, savedIds, sort]);
  const toggleSaved = (book: Book) => { const alreadySaved = savedIds.includes(book.id); setSavedIds((current) => alreadySaved ? current.filter((id) => id !== book.id) : [...current, book.id]); setSaveMessage(alreadySaved ? `${book.title} removed from saved books` : `${book.title} saved to your list`); };
  const openBook = (book: Book) => { setSelectedBookId(book.id); setPage('Book detail'); };
  const selectedBook = books.find((book) => book.id === selectedBookId);
  const borrowBook = books.find((book) => book.id === borrowBookId);
  const remainingCopies = (book: Book) => Math.max(0, book.available - borrowedBooks.filter((loan) => loan.book.id === book.id).length);
  const completeBorrow = (details: BorrowerDetails) => {
    if (!borrowBook) return;
    const borrowedOn = new Date();
    const due = new Date(borrowedOn);
    due.setDate(due.getDate() + details.days);
    setBorrowedBooks((current) => [{ ...details, id: Date.now(), book: borrowBook, borrowedOn: borrowedOn.toLocaleDateString(), dueOn: due.toLocaleDateString(), totalRent: details.days * DAILY_RENT_INR }, ...current]);
    setBorrowBookId(null);
    setPage('Borrowed books');
  };
  return <div className="app-shell">
    <aside className="sidebar"><a className="brand" href="#home"><span className="brand-icon"><LibraryBig size={20}/></span><span>libra<span className="brand-light">desk</span></span></a><div className="branch"><span className="branch-dot"/> Riverside Public Library <ChevronDown size={14}/></div><div className="nav-label">YOUR LIBRARY</div><nav>{navItems.map(({ name, icon: Icon }) => <button key={name} className={`nav-item ${page===name?'active':''}`} onClick={()=>{setPage(name);if(name==='Saved books'){setQuery('');setCategory('All subjects');}}}><Icon size={17}/>{name}{name==='Borrowed books'&&borrowedBooks.length>0&&<span className="nav-count">{borrowedBooks.length}</span>}{name==='Saved books'&&savedIds.length>0&&<span className="nav-count">{savedIds.length}</span>}</button>)}</nav><div className="side-bottom"><div className="staff-card"><div className="staff-top"><span className="staff-dot"/> TODAY AT A GLANCE</div><div className="staff-stat"><b>24</b><span>books due back</span></div><div className="staff-stat"><b>08</b><span>holds to collect</span></div><a href="#desk">Open circulation desk <ArrowRight size={13}/></a></div><div className="profile"><div className="avatar">AS</div><div><strong>Aria Shah</strong><small>Member account</small></div><ChevronDown size={15}/></div></div></aside>
    <main className="main-area"><header className="topbar"><div className="crumb">Library <span>/</span> <b>{page}</b></div><div className="top-actions"><span className="open-status"><i/> Open until 8:00 PM</span><button className="icon-btn" aria-label="Notifications"><Bell size={18}/><i className="notification-dot"/></button><div className="top-avatar">AS</div></div></header>
      {page==='Discover'? <><section className="hero"><div className="hero-copy"><div className="eyebrow"><Sparkles size={14}/> A LITTLE MORE ROOM TO WONDER</div><h1>Find your next<br/><em>favorite chapter.</em></h1><p>Explore the shelves, follow a curiosity, or find that book you meant to read.</p><div className="search-wrap"><Search size={19}/><input aria-label="Search the catalog" value={query} onChange={(e)=>setQuery(e.target.value)} placeholder="Search titles, authors, or ISBN"/><kbd><Command size={11}/> K</kbd><button aria-label="Search"><ArrowRight size={18}/></button>{query.length>1&&suggestions.length>0&&<div className="suggestions">{suggestions.map((s)=><button key={s} onClick={()=>setQuery(s)}><Search size={14}/>{s}</button>)}</div>}</div><div className="search-hint">Try <button onClick={()=>setQuery('algorithms')}>“algorithms”</button>, <button onClick={()=>setQuery('Mira Sen')}>“Mira Sen”</button> or a subject</div></div><div className="hero-art"><div className="sun-disc"/><div className="art-label"><span>THE JOY OF</span><b>getting lost<br/>in a good book</b><i>VOL. 04 · 2024</i></div><div className="book-shape book-one"/><div className="book-shape book-two"/><div className="book-shape book-three"/><div className="plant">✳</div></div></section>
      <section className="content-section"><div className="section-heading"><div><div className="eyebrow muted">A GOOD PLACE TO START</div><h2>On our readers’ minds <span>↗</span></h2></div><button className="text-link">View all books <ArrowRight size={15}/></button></div><div className="toolbar"><div className="chips">{categories.map((c)=><button key={c} onClick={()=>setCategory(c)} className={`chip ${category===c?'selected':''}`}>{c}</button>)}</div><div className="sort-tools"><button className="filter-button"><SlidersHorizontal size={15}/> Filters</button><label><ArrowDownUp size={14}/><select aria-label="Sort books" value={sort} onChange={(e)=>setSort(e.target.value)}><option>Popular</option><option>Title A–Z</option></select></label></div></div>
      {isLoading?<div className="empty">Gathering a few good reads…</div>:shown.length===0?<div className="empty">No books matched that search. Try another title or author.</div>:<div className="book-grid">{shown.map((book,index)=><BookCard key={book.id} book={book} index={index} available={remainingCopies(book)} saved={savedIds.includes(book.id)} onSave={()=>toggleSaved(book)} onOpen={()=>openBook(book)} onBorrow={()=>setBorrowBookId(book.id)}/>)}</div>}
      </section><section className="quote-strip"><div className="quote-icon">“</div><p>Libraries are not just about books. They are about the stories we haven’t found yet.</p><span>— A note from your librarians</span></section></>: page==='Saved books'? <section className="saved-page"><div className="eyebrow muted">YOUR READING LIST</div><h1>Saved books</h1><p>Keep a shortlist of books you want to find on the shelves.</p>{shown.length===0?<div className="empty saved-empty"><Bookmark size={24}/><b>No saved books yet</b><span>Tap the bookmark on any title to keep it here.</span><button className="primary-button" onClick={()=>setPage('Discover')}>Browse the catalog <ArrowRight size={15}/></button></div>:<div className="book-grid">{shown.map((book,index)=><BookCard key={book.id} book={book} index={index} available={remainingCopies(book)} saved onSave={()=>toggleSaved(book)} onOpen={()=>openBook(book)} onBorrow={()=>setBorrowBookId(book.id)}/>)}</div>}</section>: page==='Borrowed books'? <BorrowedBooksPage loans={borrowedBooks} onReturn={(id)=>setBorrowedBooks((current)=>current.filter((loan)=>loan.id!==id))}/>: page==='Book detail'&&selectedBook?<BookDetail book={selectedBook} available={remainingCopies(selectedBook)} saved={savedIds.includes(selectedBook.id)} onSave={()=>toggleSaved(selectedBook)} onBorrow={()=>setBorrowBookId(selectedBook.id)} onBack={()=>setPage('Discover')}/>: page==='Chemistry Lab'? <ChemistryLab/>: <section className="placeholder-page"><div className="eyebrow muted">LIBRADESK</div><h1>{page}</h1><p>This space is ready for your library account tools.</p><button className="primary-button" onClick={()=>setPage('Discover')}>Back to Discover <ArrowRight size={16}/></button></section>}
      <footer><span>Demo project with synthetic data.</span><div><a href="#about">About this project</a><a href="#help">Need a hand?</a><a href="#privacy">Privacy</a></div></footer>
      <span className="sr-only" role="status" aria-live="polite">{saveMessage}</span>
    </main>
    {borrowBook&&<BorrowDialog book={borrowBook} dailyRate={DAILY_RENT_INR} available={remainingCopies(borrowBook)} onClose={()=>setBorrowBookId(null)} onSubmit={completeBorrow}/>}
  </div>;
}

function BookCard({ book, index, available, saved, onSave, onOpen, onBorrow }: { book: Book; index: number; available: number; saved: boolean; onSave: () => void; onOpen: () => void; onBorrow: () => void }) {
  const location = getLocation(book);
  return <article className="book-card"><div className={`cover cover-${index % 5}`}><div className="cover-mark">{index===0?'A':index===1?'G':index===2?'✳':index===3?'DS':'Q'}</div><div className="cover-copy"><small>{book.category.toUpperCase()}</small><b>{book.title}</b><span>{book.author}</span></div><div className="cover-spine"/></div><div className="book-meta"><div className="book-category">{book.category} <span>·</span> {book.year}</div><button className="book-title-button" onClick={onOpen}>{book.title}</button><p>by {book.author}</p><div className="shelf-teaser"><MapPin size={12}/><span>{location.floor} · Shelf {book.shelfLocation}</span></div><div className="card-bottom"><span className={available>0?'available':'unavailable'}><i/>{available>0?`${available} copies on shelf`:'All copies on loan'}</span><button aria-label={saved?`Remove ${book.title} from saved books`:`Save ${book.title}`} aria-pressed={saved} onClick={onSave} className={`save-btn ${saved?'is-saved':''}`}><Bookmark size={16} fill={saved?'currentColor':'none'}/></button></div><button className="card-borrow-button" aria-label={`Borrow ${book.title}`} disabled={available===0} onClick={onBorrow}>Borrow <ArrowRight size={13}/></button>{available===0&&<span className="borrow-unavailable-note">No copy available at this time</span>}</div></article>;
}

function BookDetail({ book, available, saved, onSave, onBorrow, onBack }: { book: Book; available: number; saved: boolean; onSave: () => void; onBorrow: () => void; onBack: () => void }) {
  const location = getLocation(book);
  return <section className="book-detail-page"><button className="back-link" onClick={onBack}><ArrowLeft size={15}/> Back to catalog</button><div className="detail-layout"><div className={`detail-cover cover cover-${book.id % 5}`}><div className="cover-mark">{book.category.slice(0,1)}</div><div className="cover-copy"><small>{book.category.toUpperCase()}</small><b>{book.title}</b><span>{book.author}</span></div><div className="cover-spine"/></div><div className="detail-copy"><div className="eyebrow muted">{book.category.toUpperCase()} · {book.year}</div><h1>{book.title}</h1><p className="detail-author">by {book.author}</p><p className="detail-description">{book.description}</p><div className="detail-availability"><span className={available>0?'available':'unavailable'}><i/>{available>0?`${available} physical copies available`: 'All physical copies are currently on loan'}</span><span>ISBN {book.isbn}</span></div><div className="detail-actions"><button className={`primary-button ${saved?'saved-action':''}`} onClick={onSave}><Bookmark size={15} fill={saved?'currentColor':'none'}/>{saved?'Saved to your list':'Save this book'}</button><button className="primary-button borrow-primary" disabled={available===0} onClick={onBorrow}>Borrow this book <ArrowRight size={15}/></button></div></div></div><section className="location-panel"><div className="location-heading"><span className="location-icon"><MapPin size={19}/></span><div><div className="eyebrow muted">PHYSICAL COLLECTION</div><h2>Find it in the library</h2></div><span className="location-status">{available>0?'ON SHELF NOW':'CHECK AVAILABILITY'}</span></div><div className="location-route"><div><small>FLOOR</small><b>{location.floor}</b></div><ArrowRight size={15}/><div><small>COLLECTION</small><b>{location.wing}</b></div><ArrowRight size={15}/><div><small>BOOKSHELF</small><b>{book.shelfLocation}</b></div></div><div className="shelf-map" aria-label={`Shelf locator: ${location.wing}, aisle ${location.aisle}, shelf ${location.shelf}`}><div className="map-entrance">ENTRANCE</div><div className="map-aisles"><div className={`map-aisle ${location.aisle==='A'?'active':''}`}><span>AISLE A</span><i/><i/><i/></div><div className={`map-aisle ${location.aisle==='B'?'active':''}`}><span>AISLE B</span><i/><i/><i/></div><div className={`map-aisle ${location.aisle==='C'?'active':''}`}><span>AISLE C</span><i/><i/><i/></div><div className={`map-aisle ${location.aisle==='D'?'active':''}`}><span>AISLE D</span><i/><i/><i/></div></div><div className="map-you-are-here">YOU ARE HERE <span>●</span></div></div><p className="location-note">{available>0?`Ask a librarian for ${book.shelfLocation} if you need help finding this shelf.`:`This title’s home shelf is ${book.shelfLocation}; no copy is currently on the shelf.`}</p></section></section>;
}

function getLocation(book: Book) {
  const byCategory: Record<string, { floor: string; wing: string }> = { 'Literature': { floor: '1', wing: 'Reading Hall' }, 'Computer Science': { floor: '2', wing: 'West Wing' }, 'Science': { floor: '2', wing: 'East Wing' }, 'History': { floor: '3', wing: 'Archive Wing' } };
  const [aisleCode = 'A1'] = book.shelfLocation.split('-').slice(-1);
  const aisle = aisleCode.charAt(0);
  const shelf = aisleCode.slice(1) || '1';
  return { ...(byCategory[book.category] ?? { floor: '1', wing: 'Main Collection' }), aisle, shelf };
}

function BorrowDialog({ book, available, dailyRate, onClose, onSubmit }: { book: Book; available: number; dailyRate: number; onClose: () => void; onSubmit: (details: BorrowerDetails) => void }) {
  const [name, setName] = useState('');
  const [mobile, setMobile] = useState('');
  const [days, setDays] = useState(7);
  const total = days * dailyRate;
  const location = getLocation(book);
  return <div className="borrow-backdrop" onMouseDown={(event)=>{if(event.target===event.currentTarget)onClose();}}><section className="borrow-dialog" role="dialog" aria-modal="true" aria-labelledby="borrow-title"><button className="dialog-close" aria-label="Close borrow form" onClick={onClose}>×</button><div className="eyebrow muted">LIBRARY CHECKOUT</div><h2 id="borrow-title">Borrow this book</h2><div className="borrow-book-summary"><div className="borrow-mini-cover">{book.category.slice(0,1)}</div><div><b>{book.title}</b><span>by {book.author}</span><small><MapPin size={11}/>{location.floor} · Shelf {book.shelfLocation}</small></div></div><form onSubmit={(event)=>{event.preventDefault();onSubmit({name:name.trim(),mobile:mobile.trim(),days});}}><label>Borrower name<input autoFocus required maxLength={80} value={name} onChange={(event)=>setName(event.target.value)} placeholder="Your full name"/></label><label>Mobile number<input type="tel" required pattern="[0-9+() -]{10,16}" value={mobile} onChange={(event)=>setMobile(event.target.value)} placeholder="10 digit mobile number"/><small>Enter 10–16 digits, spaces, or +country code.</small></label><label>Borrow for (days)<div className="days-control"><button type="button" aria-label="Decrease days" disabled={days<=1} onClick={()=>setDays((value)=>Math.max(1,value-1))}>−</button><input aria-label="Borrow duration in days" type="number" min={1} max={30} value={days} onChange={(event)=>setDays(Math.min(30,Math.max(1,Number(event.target.value)||1)))}/><button type="button" aria-label="Increase days" disabled={days>=30} onClick={()=>setDays((value)=>Math.min(30,value+1))}>+</button></div></label><div className="rent-summary"><span>Daily rent</span><b>₹{dailyRate} × {days} days</b><strong>₹{total}</strong></div><p className="borrow-demo-note">Demo checkout: no payment is collected. Loan and borrower details stay in this browser session.</p><button className="borrow-submit" type="submit" disabled={available<=0}>Confirm borrow · ₹{total}</button></form></section></div>;
}

function BorrowedBooksPage({ loans, onReturn }: { loans: BorrowedBook[]; onReturn: (id: number) => void }) {
  return <section className="borrowed-page"><div className="eyebrow muted">YOUR CHECKOUTS</div><h1>Borrowed books</h1><p>Active loans, due dates, and the rent calculated at checkout.</p>{loans.length===0?<div className="empty saved-empty"><BookOpen size={25}/><b>No books borrowed yet</b><span>Choose a book from Discover and use Borrow to start a loan.</span></div>:<div className="loan-list">{loans.map((loan)=>{const location=getLocation(loan.book);return <article className="loan-card" key={loan.id}><div className={`loan-cover cover-${loan.book.id%5}`}><span>{loan.book.category.slice(0,1)}</span></div><div className="loan-book-info"><div className="eyebrow muted">{loan.book.category.toUpperCase()}</div><h2>{loan.book.title}</h2><p>by {loan.book.author}</p><div className="shelf-teaser"><MapPin size={12}/><span>{location.floor} · Shelf {loan.book.shelfLocation}</span></div></div><div className="loan-details"><span>Borrower</span><b>{loan.name}</b><small>•••• {loan.mobile.slice(-4)}</small></div><div className="loan-details"><span>Due date</span><b>{loan.dueOn}</b><small>{loan.days} day{loan.days===1?'':'s'} borrowed</small></div><div className="loan-rent"><span>Estimated rent</span><b>₹{loan.totalRent}</b><small>₹{DAILY_RENT_INR}/day</small></div><button className="return-button" onClick={()=>onReturn(loan.id)}>Return</button></article>;})}</div>}</section>;
}

type ElementInfo = { symbol: string; name: string; number: number; mass: number; category: string; row: number; column: number; note: string };
const ELEMENT_DATA: ReadonlyArray<readonly [string, string, number]> = [
  ['H','Hydrogen',1.008],['He','Helium',4.0026],['Li','Lithium',6.94],['Be','Beryllium',9.0122],['B','Boron',10.81],['C','Carbon',12.011],['N','Nitrogen',14.007],['O','Oxygen',15.999],['F','Fluorine',18.998],['Ne','Neon',20.18],
  ['Na','Sodium',22.99],['Mg','Magnesium',24.305],['Al','Aluminium',26.982],['Si','Silicon',28.085],['P','Phosphorus',30.974],['S','Sulfur',32.06],['Cl','Chlorine',35.45],['Ar','Argon',39.948],
  ['K','Potassium',39.098],['Ca','Calcium',40.078],['Sc','Scandium',44.956],['Ti','Titanium',47.867],['V','Vanadium',50.942],['Cr','Chromium',51.996],['Mn','Manganese',54.938],['Fe','Iron',55.845],['Co','Cobalt',58.933],['Ni','Nickel',58.693],['Cu','Copper',63.546],['Zn','Zinc',65.38],['Ga','Gallium',69.723],['Ge','Germanium',72.63],['As','Arsenic',74.922],['Se','Selenium',78.971],['Br','Bromine',79.904],['Kr','Krypton',83.798],
  ['Rb','Rubidium',85.468],['Sr','Strontium',87.62],['Y','Yttrium',88.906],['Zr','Zirconium',91.224],['Nb','Niobium',92.906],['Mo','Molybdenum',95.95],['Tc','Technetium',98],['Ru','Ruthenium',101.07],['Rh','Rhodium',102.906],['Pd','Palladium',106.42],['Ag','Silver',107.868],['Cd','Cadmium',112.414],['In','Indium',114.818],['Sn','Tin',118.71],['Sb','Antimony',121.76],['Te','Tellurium',127.6],['I','Iodine',126.904],['Xe','Xenon',131.293],
  ['Cs','Caesium',132.905],['Ba','Barium',137.327],['La','Lanthanum',138.905],['Ce','Cerium',140.116],['Pr','Praseodymium',140.908],['Nd','Neodymium',144.242],['Pm','Promethium',145],['Sm','Samarium',150.36],['Eu','Europium',151.964],['Gd','Gadolinium',157.25],['Tb','Terbium',158.925],['Dy','Dysprosium',162.5],['Ho','Holmium',164.93],['Er','Erbium',167.259],['Tm','Thulium',168.934],['Yb','Ytterbium',173.045],['Lu','Lutetium',174.967],['Hf','Hafnium',178.49],['Ta','Tantalum',180.948],['W','Tungsten',183.84],['Re','Rhenium',186.207],['Os','Osmium',190.23],['Ir','Iridium',192.217],['Pt','Platinum',195.084],['Au','Gold',196.967],['Hg','Mercury',200.592],['Tl','Thallium',204.38],['Pb','Lead',207.2],['Bi','Bismuth',208.98],['Po','Polonium',209],['At','Astatine',210],['Rn','Radon',222],
  ['Fr','Francium',223],['Ra','Radium',226],['Ac','Actinium',227],['Th','Thorium',232.038],['Pa','Protactinium',231.036],['U','Uranium',238.029],['Np','Neptunium',237],['Pu','Plutonium',244],['Am','Americium',243],['Cm','Curium',247],['Bk','Berkelium',247],['Cf','Californium',251],['Es','Einsteinium',252],['Fm','Fermium',257],['Md','Mendelevium',258],['No','Nobelium',259],['Lr','Lawrencium',266],['Rf','Rutherfordium',267],['Db','Dubnium',268],['Sg','Seaborgium',269],['Bh','Bohrium',270],['Hs','Hassium',277],['Mt','Meitnerium',278],['Ds','Darmstadtium',281],['Rg','Roentgenium',282],['Cn','Copernicium',285],['Nh','Nihonium',286],['Fl','Flerovium',289],['Mc','Moscovium',290],['Lv','Livermorium',293],['Ts','Tennessine',294],['Og','Oganesson',294],
];
const METALLOIDS = new Set(['B','Si','Ge','As','Sb','Te']);
const NONMETALS = new Set(['H','C','N','O','P','S','Se']);
const POST_TRANSITION = new Set(['Al','Ga','In','Sn','Tl','Pb','Bi','Po','Nh','Fl','Mc','Lv']);
function positionFor(number: number): { row: number; column: number; group: number } {
  if (number===1) return { row:1,column:1,group:1 };
  if (number===2) return { row:1,column:18,group:18 };
  if (number>=3&&number<=4) return { row:2,column:number-2,group:number-2 };
  if (number>=5&&number<=10) return { row:2,column:number+8,group:number+8 };
  if (number>=11&&number<=12) return { row:3,column:number-10,group:number-10 };
  if (number>=13&&number<=18) return { row:3,column:number,group:number };
  if (number>=19&&number<=36) return { row:4,column:number-18,group:number-18 };
  if (number>=37&&number<=54) return { row:5,column:number-36,group:number-36 };
  if (number>=55&&number<=56) return { row:6,column:number-54,group:number-54 };
  if (number>=57&&number<=71) return { row:8,column:number-53,group:3 };
  if (number>=72&&number<=86) return { row:6,column:number-68,group:number-68 };
  if (number>=87&&number<=88) return { row:7,column:number-86,group:number-86 };
  if (number>=89&&number<=103) return { row:9,column:number-85,group:3 };
  return { row:7,column:number-100,group:number-100 };
}
const ELEMENTS: ElementInfo[] = ELEMENT_DATA.map(([symbol,name,mass],index)=>{
  const number=index+1;const position=positionFor(number);const category=number>=57&&number<=71?'lanthanide':number>=89&&number<=103?'actinide':NONMETALS.has(symbol)?'nonmetal':METALLOIDS.has(symbol)?'metalloid':position.group===1?'alkali':position.group===2?'alkaline':position.group===17?'halogen':position.group===18?'noble':POST_TRANSITION.has(symbol)?'post-transition':position.group>=3&&position.group<=12?'transition':'post-transition';
  return {symbol,name,mass,number,...position,category,note:`${name} is classified as a ${category.replace('-',' ')} element. Its standard atomic mass is used in formula calculations.`};
});

export function calculateMolarMass(formula: string): number | null {
  const text = formula.replace(/\s/g, '');
  const tokens = text.match(/[A-Z][a-z]?|\d+|[()]/g) ?? [];
  if (!text || tokens.join('') !== text) return null;
  const totals = [0];
  for (let i = 0; i < tokens.length; i += 1) {
    const token = tokens[i];
    if (token === '(') { totals.push(0); continue; }
    if (token === ')') {
      if (totals.length === 1) return null;
      const nestedMass = totals.pop() ?? 0;
      const multiplier = /^\d+$/.test(tokens[i + 1] ?? '') ? Number(tokens[++i]) : 1;
      totals[totals.length - 1] += nestedMass * multiplier;
      continue;
    }
    if (/^\d+$/.test(token)) return null;
    const element = ELEMENTS.find((item) => item.symbol === token);
    if (!element) return null;
    const multiplier = /^\d+$/.test(tokens[i + 1] ?? '') ? Number(tokens[++i]) : 1;
    if (multiplier < 1) return null;
    totals[totals.length - 1] += element.mass * multiplier;
  }
  return totals.length === 1 ? totals[0] : null;
}

function ChemistryLab() {
  const [selectedSymbol, setSelectedSymbol] = useState('O');
  const [elementQuery, setElementQuery] = useState('');
  const [elementMessage, setElementMessage] = useState('');
  const [formula, setFormula] = useState('H2O');
  const [sampleMass, setSampleMass] = useState(18.015);
  const [solutionVolume, setSolutionVolume] = useState(250);
  const selected = ELEMENTS.find((element) => element.symbol === selectedSymbol) ?? ELEMENTS[0];
  const molarMass = calculateMolarMass(formula);
  const molarity = molarMass && solutionVolume > 0 ? sampleMass / molarMass / (solutionVolume / 1000) : null;
  const findElement = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const match = ELEMENTS.find((element) => element.symbol.toLowerCase() === elementQuery.trim().toLowerCase() || element.name.toLowerCase() === elementQuery.trim().toLowerCase());
    if (match) { setSelectedSymbol(match.symbol); setElementMessage(`${match.name} selected.`); }
    else setElementMessage('No element found. Search by full name or symbol.');
  };
  return <section className="chemistry-page">
    <div className="chemistry-hero"><div className="chemistry-orbit orbit-one"/><div className="chemistry-orbit orbit-two"/><div className="eyebrow"><FlaskConical size={14}/> LIBRADESK LEARNING STUDIO</div><h1>Chemistry Lab</h1><p>A clear, hands-on reference for exploring elements, formulas, and solution concentration.</p><div className="chemistry-hero-meta"><span>118 elements</span><span>Formula tools</span><span>Learning notes</span></div></div>
    <div className="chemistry-layout">
      <article className="chem-panel periodic-panel">
        <div className="chem-panel-heading"><div><span className="chem-kicker">01 / ELEMENTS</span><h2>Periodic table explorer</h2><p>All 118 elements, arranged by period and group. Select a tile or search by name or symbol.</p></div><span className="element-count">118 ELEMENTS</span></div>
        <form className="element-search" onSubmit={findElement}><Search size={15}/><input aria-label="Find an element" value={elementQuery} onChange={(event)=>setElementQuery(event.target.value)} placeholder="Find element by name or symbol"/><button>Find</button></form>
        {elementMessage&&<p className="element-search-message" role="status">{elementMessage}</p>}
        <div className="periodic-table" aria-label="Complete periodic table of 118 elements">
          <div className="series-marker" style={{gridColumn:3,gridRow:6}}>57–71</div><div className="series-marker" style={{gridColumn:3,gridRow:7}}>89–103</div>
          <div className="series-label" style={{gridColumn:'1 / span 3',gridRow:8}}>LANTHANIDES</div><div className="series-label" style={{gridColumn:'1 / span 3',gridRow:9}}>ACTINIDES</div>
          {ELEMENTS.map((element)=><button key={element.symbol} className={`element-tile ${element.category} ${selectedSymbol===element.symbol?'selected':''}`} style={{gridColumn:element.column,gridRow:element.row}} aria-label={`${element.name}, atomic number ${element.number}`} aria-pressed={selectedSymbol===element.symbol} onClick={()=>setSelectedSymbol(element.symbol)}><small>{element.number}</small><b>{element.symbol}</b><span>{element.mass.toFixed(element.mass<10?3:2)}</span></button>)}
        </div>
        <p className="mass-note">For elements without a standard atomic weight, the displayed value is a representative isotope mass number.</p>
        <div className="element-detail"><div className={`element-large ${selected.category}`}><small>{selected.number}</small><b>{selected.symbol}</b><span>{selected.category.replace('-',' ')}</span></div><div><div className="chem-kicker">SELECTED ELEMENT</div><h3>{selected.name}</h3><p>Atomic number {selected.number} · Atomic mass {selected.mass} u</p><span>{selected.note}</span></div></div>
        <div className="element-legend"><span><i className="nonmetal"/>Nonmetal</span><span><i className="alkali"/>Alkali metal</span><span><i className="transition"/>Transition metal</span><span><i className="metalloid"/>Metalloid</span><span><i className="halogen"/>Halogen</span><span><i className="noble"/>Noble gas</span><span><i className="lanthanide"/>Lanthanide</span><span><i className="actinide"/>Actinide</span></div>
      </article>
      <div className="chem-tools"><article className="chem-panel calculator-panel"><div className="chem-panel-heading"><div><span className="chem-kicker">02 / FORMULA TOOL</span><h2>Molar mass calculator</h2><p>Enter a formula, including parentheses such as Ca(OH)2.</p></div></div><label className="chem-field">Chemical formula<input aria-label="Chemical formula" value={formula} onChange={(event)=>setFormula(event.target.value)} placeholder="e.g. H2O"/></label><div className={`formula-result ${molarMass===null?'invalid':''}`}><span>{molarMass===null?'Check formula symbols and brackets':'Calculated molar mass'}</span><b>{molarMass===null?'—':`${molarMass.toFixed(3)} g/mol`}</b></div><div className="formula-examples"><span>Try:</span>{['H2O','CO2','NaCl','Ca(OH)2','Fe2O3','CuSO4'].map((example)=><button key={example} onClick={()=>setFormula(example)}>{example}</button>)}</div></article>
        <article className="chem-panel molarity-panel"><div className="chem-panel-heading"><div><span className="chem-kicker">03 / SOLUTION TOOL</span><h2>Molarity calculator</h2><p>Estimate concentration from solute mass and final solution volume.</p></div></div><div className="molarity-fields"><label className="chem-field">Solute mass<input aria-label="Solute mass in grams" type="number" min="0" step="any" value={sampleMass} onChange={(event)=>setSampleMass(Math.max(0,Number(event.target.value)))}/><small>grams</small></label><label className="chem-field">Final volume<input aria-label="Solution volume in milliliters" type="number" min="0" step="any" value={solutionVolume} onChange={(event)=>setSolutionVolume(Math.max(0,Number(event.target.value)))}/><small>milliliters</small></label></div><div className="molarity-result"><span>Concentration (M)</span><b>{molarity===null||!Number.isFinite(molarity)?'—':`${molarity.toFixed(3)} mol/L`}</b><small>Uses {molarMass===null?'a valid formula':`${molarMass.toFixed(3)} g/mol for ${formula}`}</small></div></article></div>
    </div>
    <aside className="chem-safety"><span className="safety-mark">!</span><div><b>Lab safety reminder</b><p>These tools are for classroom calculations. For hands-on work, follow your institution’s procedure and Safety Data Sheet, use appropriate protective equipment, and never mix unknown substances.</p></div><span className="safety-tag">LEARN SAFELY</span></aside>
  </section>;
}
