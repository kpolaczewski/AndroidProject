from fastapi import FastAPI, HTTPException
from fastapi.responses import FileResponse
from pydantic import BaseModel
from typing import List, Optional
import json
import os

app = FastAPI()

with open("metadata.json", "r", encoding="utf-8") as f:
    books = json.load(f)

class Book(BaseModel):
    id: str
    title: str
    author: str
    description: str
    filename: str
    cover_url: Optional[str]

@app.get("/books", response_model=List[Book])
def get_books(search: Optional[str] = None):
    """
    Returns a list of books.
    Optional search by title or author (case insensitive).
    """
    if not search:
        return books
    search_lower = search.lower()
    filtered = [
        book for book in books
        if search_lower in book["title"].lower() or search_lower in book["author"].lower()
    ]
    return filtered

@app.get("/books/{book_id}", response_model=Book)
def get_book(book_id: str):
    for book in books:
        if book["id"] == book_id:
            return book
    raise HTTPException(status_code=404, detail="Book not found")

@app.get("/books/{book_id}/download")
def download_book(book_id: str):
    for book in books:
        if book["id"] == book_id:
            file_path = os.path.join("books", book["filename"])
            if os.path.exists(file_path):
                return FileResponse(file_path, media_type="application/epub+zip", filename=book["filename"])
            else:
                raise HTTPException(status_code=404, detail="EPUB file not found")
    raise HTTPException(status_code=404, detail="Book not found")
