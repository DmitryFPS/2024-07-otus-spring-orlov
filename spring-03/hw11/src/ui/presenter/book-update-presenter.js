import React from 'react'
import {Header} from '../service/header-service.js'
import ReactDOM from 'react-dom'
import App from './books-presenter.js'
import _ from 'lodash'


export default class BookUpdatePresenter extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            book: props.book, authors: [], genres: [],
            title: props.book.title || '', authorId: props.book.author.id || '',
            genreIds: props.book.genres.map(genre => genre.id) || []
        }

        this.handleAuthorChange = this.handleAuthorChange.bind(this)
        this.handleGenreChange = this.handleGenreChange.bind(this)
        this.handleTitleChange = this.handleTitleChange.bind(this)
        this.handleBooksClick = this.handleBooksClick.bind(this)
        this.handleSubmit = this.handleSubmit.bind(this)
    }

    async componentDidMount() {
        await this.fetchAuthors()
        await this.fetchGenres()
    }

    async fetchAuthors() {
        try {
            const response = await fetch('/api/v1/author', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            })

            if (!response.ok) {
                throw new Error('Ошибка получения авторов')
            }

            const authors = await response.json()
            this.setState({authors})
        } catch (error) {
            console.error('Ошибка получения авторов:', error)
        }
    }

    async fetchGenres() {
        try {
            const response = await fetch('/api/v1/genre', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            })

            if (!response.ok) {
                throw new Error('Ошибка получения жанров')
            }

            const genres = await response.json()
            this.setState({genres})
        } catch (error) {
            console.error('Ошибка получения жанров:', error)
        }
    }

    handleAuthorChange(event) {
        this.setState({authorId: event.target.value})
    }

    handleGenreChange(event) {
        const genreId = event.target.value

        if (event.target.checked) {
            this.setState(state => ({
                genreIds: [...this.state.genreIds, genreId]
            }))
        } else {
            this.setState(state => ({
                genreIds: this.state.genreIds.filter(id => id !== genreId)
            }))
        }
    }

    handleTitleChange(event) {
        this.setState({title: event.target.value})
    }

    async handleSubmit(event) {
        event.preventDefault()

        const data = {
            title: this.state.title,
            authorId: this.state.authorId,
            genreIds: this.state.genreIds,
        }

        if (_.isEqual({
            title: this.state.book?.title,
            authorId: this.state.book?.author?.id,
            genreIds: this.state.book?.genres?.map(genre => genre?.id)
        }, data)) {
            this.handleBooksClick()
            return
        }

        try {
            const response = await fetch(`/api/v1/book/${this.state.book?.id}`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data),
            })

            if (!response.ok) {
                throw new Error('Ошибка обновления книги')
            }

            this.handleBooksClick()
        } catch (error) {
            console.error('Ошибка обновления книги:', error)
        }
    }

    render() {
        const {authors, genres, title, authorId, genreIds} = this.state

        if (authors.length === 0 || genres.length === 0) {
            return (
                <div>Загрузка...</div>
            )
        }

        return (
            <form onSubmit={this.handleSubmit}>
                <Header title={'Update book'}/>
                <table className='persons-table'>
                    <tbody>
                    <tr className='persons-table_item'>
                        <td className='persons-table_item'>
                            <div className="row">
                                <label>Title:</label>
                                <input name="title" type="text" value={title} onChange={this.handleTitleChange}
                                       required={true}/>
                            </div>
                        </td>
                        <td className='persons-table_item'>
                            <label>
                                <select value={authorId} onChange={this.handleAuthorChange} required>
                                    <option value="">Выберите автора</option>
                                    {authors.map(author => (
                                        <option key={author.id} value={author.id}>{author.fullName}</option>
                                    ))}
                                </select>
                            </label>
                        </td>
                        <td className='persons-table_item'>
                            {genres.map(genre => (
                                <div key={genre.id}>
                                    <input type="checkbox"
                                           id={`genre-${genre.id}`}
                                           value={genre.id}
                                           checked={genreIds.includes(genre.id)}
                                           onChange={this.handleGenreChange}/>
                                    <label htmlFor={`genre-${genre.id}`}>{genre.name}</label>
                                </div>
                            ))}
                        </td>
                    </tr>
                    </tbody>
                </table>
                <table>
                    <tbody>
                    <tr>
                        <td>
                            <div className='persons-table_button'>
                                <button type="submit">Обновить книгу</button>
                            </div>
                        </td>

                        <td>
                            <div className='persons-table_button'>
                                <button type="button" onClick={() => this.handleBooksClick()}>Отмена</button>
                            </div>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </form>
        )
    }

    handleBooksClick() {
        ReactDOM.render(<App/>, document.getElementById('content'))
        this.state = {book: '', title: '', authorId: '', authors: [], genres: [], genreIds: []}
    }
}
