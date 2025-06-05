import React, { useEffect, useState } from 'react'

type City = {
  id: string
  name: string
}

const CityList: React.FC = () => {
  const [cities, setCities] = useState<City[]>([])
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    fetch('/api/city')
      .then(res => {
        if (!res.ok) throw new Error('Ошибка загрузки данных')
        return res.json()
      })
      .then(data => setCities(data))
      .catch(err => setError(err.message))
  }, [])

  return (
    <div>
      <h2>Список городов</h2>
      {error && <p style={{ color: 'red' }}>Ошибка: {error}</p>}
      <ul>
        {cities.map(city => (
          <li key={city.id}>{city.name}</li>
        ))}
      </ul>
    </div>
  )
}

export default CityList
